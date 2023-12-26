package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    private final PasswordEncoder passwordEncoder;
    private final TeamRepository teamRepository;

    /**
     * 회원가입
     * @param accountRequest 회원가입 DTO
     * @return Memp data
     */
    public Memp register(AccountRequest accountRequest) {

        // 사용자 ROLE 지정 (default : ROLE_USER)
        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));

        // 신규 등록할 사용자 객체 생성
        Memp memp = Memp.builder()
                .name(accountRequest.getName())
                .rank(accountRequest.getRank())
                .position(accountRequest.getPosition())
                .team(teamRepository.findById(accountRequest.getTeamId()).get())
                .username(accountRequest.getUsername())
                .password(accountRequest.getPassword())
                .authorities(authoritiesSet)
                .build();

        // 패스워드 인코딩
        memp.encodePassword(passwordEncoder);

        // 사용자 DB 저장
        return mempRepository.save(memp);
    }

    /**
     * 비밀번호 재설정
     * @param resetPasswordRequest 비밀번호 재설정 DTO
     * @return Memp 데이터
     */
    public Memp savePassword(ResetPasswordRequest resetPasswordRequest){

        // 현재 로그인한 유저 객체 받아옴
        Memp memp = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 신규 패스워드로 저장, 인코딩
        memp.setPassword(resetPasswordRequest.getPassword());
        memp.encodePassword(passwordEncoder);

        return mempRepository.save(memp);
    }

    /**
     * 사용자 정보 변경
     * @param modifyUserinfoRequest 사용자 정보 변경 DTO
     * @return Memp data 저장
     */
    public Memp saveUserInfo(ModifyUserinfoRequest modifyUserinfoRequest){

        // 현재 로그인한 유저 객체 받아옴
        Memp memp = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 팀이 변경되었다면
        if (! memp.getTeam().getId().equals(modifyUserinfoRequest.getTeamId())){

            // 기존 사용자 active false처리
            memp.setActive(false);
            mempRepository.save(memp);

            // 사용자 ROLE 지정 (defualt: ROLE_USER)
            Set<Authority> authoritiesSet = new HashSet<>();
            authoritiesSet.add(Authority.of("ROLE_USER"));

            // ERM DB에 해당 유저를 신규 등록한다.

            // 인사정보 DB에서 로컬 ERM DB와 매칭되는 팀 정보 가져옴, 없을 경우 에러 반환
            Team team = teamRepository.findById(modifyUserinfoRequest.getTeamId())
                    .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

            // 사용자별 캘린더 색상 랜덤 지정
            Random rand = new Random();
            String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

            // 신규 유저 객체 생성, 저장
            Memp newMemp = Memp.builder()
                    .name(memp.getName())
                    .rank("엔지니어") // default
                    .position("팀원") // default
                    .team(team)
                    .username(memp.getUsername())
                    .password(memp.getPassword())
                    .calenderColor(randomColor)
                    .authorities(authoritiesSet)
                    .active(true)
                    .build();

            //  직책 저장, 팀장일 경우 ADMIN 권한 부여
            if (modifyUserinfoRequest.getPosition().equals("팀장")){
                newMemp.getAuthorities().add(Authority.of("ROLE_ADMIN"));
            }
            else{
                newMemp.getAuthorities().remove(Authority.of("ROLE_ADMIN"));
            }
            newMemp.setRank(modifyUserinfoRequest.getRank());
            newMemp.setPosition(modifyUserinfoRequest.getPosition());

            return mempRepository.save(newMemp);

        }
        // 팀이 변경되지 않았다면
        else {
            //  직책 저장, 팀장일 경우 ADMIN 권한 부여
            if (modifyUserinfoRequest.getPosition().equals("팀장")){
                memp.getAuthorities().add(Authority.of("ROLE_ADMIN"));
            }
            else{
                memp.getAuthorities().remove(Authority.of("ROLE_ADMIN"));
            }
            // 변경할 직급 저장
            memp.setRank(modifyUserinfoRequest.getRank());
            memp.setPosition(modifyUserinfoRequest.getPosition());

            return mempRepository.save(memp);

        }

    }

}
