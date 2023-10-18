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

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MempRepository mempRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamRepository teamRepository;

    public Memp register(AccountRequest accountRequest) {

        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));


        Memp memp = Memp.builder()
                .name(accountRequest.getName())
                .rank(accountRequest.getRank())
                .position(accountRequest.getPosition())
                .team(teamRepository.findById(accountRequest.getTeamId()).get())
                .username(accountRequest.getUsername())
                .password(accountRequest.getPassword())
                .authorities(authoritiesSet)
                .build();

        memp.encodePassword(passwordEncoder);

        return mempRepository.save(memp);
    }

    public Memp savePassword(ResetPasswordRequest resetPasswordRequest){

        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        memp.setPassword(resetPasswordRequest.getPassword());
        memp.encodePassword(passwordEncoder);

        return mempRepository.save(memp);
    }

    public Memp saveUserInfo(ModifyUserinfoRequest modifyUserinfoRequest){

        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        memp.setTeam(teamRepository.findById(modifyUserinfoRequest.getTeamId()).get());
        memp.setRank(modifyUserinfoRequest.getRank());

//        팀장일 경우 ADMIN 권한 부여
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
