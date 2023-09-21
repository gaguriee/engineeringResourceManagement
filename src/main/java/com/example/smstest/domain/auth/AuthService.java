package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.Enum.Role;
import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MempRepository mempRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamRepository teamRepository;

    public Memp register(AccountRequest accountRequest) {
        Memp memp = Memp.builder()
                .name(accountRequest.getName())
                .rank(accountRequest.getRank())
                .position(accountRequest.getPosition())
                .team(teamRepository.findById(accountRequest.getTeamId()).get())
                .username(accountRequest.getUsername())
                .password(accountRequest.getPassword())
                .role(Role.valueOf("USER"))
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
        memp.setPosition(modifyUserinfoRequest.getPosition());

        return mempRepository.save(memp);
    }

}
