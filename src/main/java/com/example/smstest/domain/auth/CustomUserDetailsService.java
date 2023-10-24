package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.employee.entity.Employee;
import com.example.smstest.employee.repository.EmployeeRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
     * 사용자 인증 작업 진행 ( 정보 검색 -> 반환 -> 예외처리-UsernameNotFoundException )
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

        Optional<Memp> memp = mempRepository.findByUsername(username);
        if (memp.isPresent()){
            return new User(memp.get().getUsername(), memp.get().getPassword(),
                    memp.get().getAuthorities().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                            .collect(Collectors.toSet()));
        }


        // 인증 시도한 사용자 객체 반환, 없을 경우 에러 반환 ( 인사 DB 내 미존재 username일 경우 로그인 안됨 )
        Employee employee = employeeRepository.findByUserid(username);

        if (employee == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // 사용자별 캘린더 색상 랜덤 지정
        Random rand = new Random();
        String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

        // 사용자 ROLE 지정 (defualt: ROLE_USER)
        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));

        // 로그인 한 username의 정보가 인사정보 DB에는 있지만 ERM DB에는 없는 경우 신규 등록
        memp = mempRepository.findByUsername(username);
        if (memp.isEmpty()) {

            // 인사정보 DB에서 팀 정보 가져옴, 없을 경우 에러 반환
            Team team = teamRepository.findByName(employee.getDepartment().getDeptname())
                    .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

            // 신규 유저 객체 생성, 저장
            memp = Optional.ofNullable(Memp.builder()
                    .name(employee.getEmpname())
                    .rank("엔지니어")
                    .position("팀원")
                    .team(team)
                    .username(employee.getUserid())
                    .password(basicPassword)
                    .calenderColor(randomColor)
                    .authorities(authoritiesSet)
                    .build());

            try{
                mempRepository.save(memp.get());
            }
            catch (Exception e){
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }

        // 유저 인증 후 해당 유저 정보 반환
        return new User(memp.get().getUsername(), memp.get().getPassword(),
                memp.get().getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toSet()));
    }
}
