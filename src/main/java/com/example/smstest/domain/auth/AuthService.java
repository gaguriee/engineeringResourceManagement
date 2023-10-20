package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
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
        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 변경할 팀, 직급 저장
        memp.setTeam(teamRepository.findById(modifyUserinfoRequest.getTeamId()).get());
        memp.setRank(modifyUserinfoRequest.getRank());

        //  직책 저장, 팀장일 경우 ADMIN 권한 부여
        if (modifyUserinfoRequest.getPosition().equals("팀장")){
            memp.getAuthorities().add(Authority.of("ROLE_ADMIN"));
        }
        else{
            memp.getAuthorities().remove(Authority.of("ROLE_ADMIN"));
        }
        memp.setPosition(modifyUserinfoRequest.getPosition());

        return mempRepository.save(memp);
    }

}
