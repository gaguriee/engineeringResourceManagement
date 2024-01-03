package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.external.employee.Employee;
import com.example.smstest.external.employee.EmployeeRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * 사용자 상호작용과 관련된 Service
 * METHOD : 로그인, 회원가입, 비밀번호 재설정, 사용자 정보 변경
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    /**
     * [ 비밀번호 재설정 ]
     *
     * @param resetPasswordRequest 비밀번호 재설정 DTO
     */
    public void savePassword(ResetPasswordRequest resetPasswordRequest){

        // 현재 로그인한 유저 객체 받아옴
        Memp memp = mempRepository.findFirstByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 신규 패스워드로 저장, 인코딩
        memp.setPassword(resetPasswordRequest.getPassword());
        memp.encodePassword(passwordEncoder);

        mempRepository.save(memp);
    }

    /**
     * [ 부서 이동 신청 ]
     * @param memberId
     * @return
     * @throws CustomException
     */
    public String organizationChange(Long memberId) throws CustomException {
        Optional<Memp> mempOptional = mempRepository.findById(memberId);
        if (mempOptional.isPresent()) {
            Memp currentMemp = mempOptional.get();

            Employee employee = employeeRepository.findFirstByUserstatusAndUserid(1, currentMemp.getUsername());

            if (employee != null) {
                // 부서이동이 있을 때 (인사 DB와 ERM DB의 팀이 일치하지 않을 때)
                if (!currentMemp.getTeam().getName().equals(employee.getDepartment().getDeptname())) {
                    currentMemp.setActive(false);
                    mempRepository.save(currentMemp);

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
                            .password(currentMemp.getPassword())
                            .calenderColor(randomColor)
                            .authorities(authoritiesSet)
                            .authorities(authoritiesSet)
                            .active(true)
                            .build();

                    mempRepository.save(newMemp);
                    return "부서 이동이 성공적으로 처리되었습니다.";
                } else {
                    return "부서 이동 이력이 존재하지 않습니다.";
                }
            } else {
                // 인사연동 DB에 User status가 1이고 username이 동일한 유저 데이터가 존재하지 않음
                return "인사 DB에 해당 사용자 정보 미존재. 관리자에 문의해주세요.";
            }
        } else {
            // 현재 멤버의 정보를 로컬 DB에서 찾을 수 없음
            return "부서 이동 처리 중 오류가 발생했습니다. 관리자에 문의해주세요.";
        }
    }
}
