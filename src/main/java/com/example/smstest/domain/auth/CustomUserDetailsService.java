package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.external.employee.Employee;
import com.example.smstest.external.employee.EmployeeRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자가 로그인 시 자동 실행
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;

    private final EmployeeRepository employeeRepository;

    // 소만사 기본 Password
    @Value("${somansa.password}")
    private String basicPassword;

    /**
     * 사용자 인증 작업 진행 ( 정보 검색 -> 반환 -> 예외 처리 - UsernameNotFoundException )
     * @param username the username identifying the user whose data is required.
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*
        Employee => 인사 DB에서 가져오는 사용자 data
        Memp => ERM DB에서 가져오는 사용자 data
         */

        // 1. 먼저 로컬 ERM DB 내에서 user id로 기존 회원을 검색한다. 해당 user id가 존재하며 active=true 상태일 경우 바로 로그인을 진행한다.
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Optional<Memp> memp = mempRepository.findFirstByUsernameAndActiveTrue(username);
        if (memp.isPresent()){
            return saveCurrentUser(currentTimestamp, memp.get());
        }

        // 2. 로컬 ERM DB 내 존재하지 않는 유저일 경우 (ex 신규 유저), 소만사 인사 DB 내에서 user id로 해당 회원을 검색한다.
        // 인증 시도한 사용자 객체 반환, 없을 경우 에러 반환 ( 인사 DB에도 존재하지 않는 username이거나 퇴사했을 경우 (userstatus=0) 로그인 안됨 )
        Employee employee = employeeRepository.findFirstByUserstatusAndUserid(1, username);

        if (employee == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // 사용자별 캘린더 색상 랜덤 지정
        Random rand = new Random();
        String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

        // 사용자 ROLE 지정 (defualt: ROLE_USER)
        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));

        // ERM DB에 해당 유저를 신규 등록한다.

        // 인사정보 DB에서 로컬 ERM DB와 매칭되는 팀 정보 가져옴, 없을 경우 에러 반환
        Team team = teamRepository.findFirstByName(employee.getDepartment().getDeptname())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        // 신규 유저 객체 생성, 저장
        Memp newMemp = Memp.builder()
                .name(employee.getEmpname())
                .team(team)
                .username(employee.getUserid())
                .password(basicPassword)
                .calenderColor(randomColor)
                .authorities(authoritiesSet)
                .authorities(authoritiesSet)
                .active(true)
                .build();

        try{
            mempRepository.save(newMemp);
        }
        catch (Exception e){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return saveCurrentUser(currentTimestamp, newMemp);
    }

    /**
     * 신규 유저 DB에 저장하기
     * @param currentTimestamp
     * @param currentUser
     * @return
     */
    @NotNull
    private UserDetails saveCurrentUser(Timestamp currentTimestamp, Memp currentUser) {
        currentUser.setLastLoginAt(currentTimestamp);
        mempRepository.save(currentUser);
        return new User(currentUser.getUsername(), currentUser.getPassword(),
                currentUser.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                        .collect(Collectors.toSet()));
    }
}
