package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MempRepository mempRepository;
    private final PasswordEncoder passwordEncoder;

    public Memp save(AccountRequest accountRequest){

        Memp memp = mempRepository.findById(accountRequest.getUserId()).get();

        memp.setUsername(accountRequest.getUsername());
        memp.setPassword(accountRequest.getPassword());
        memp.encodePassword(passwordEncoder);

        return mempRepository.save(memp);
    }


    public Memp savePassword(ResetPasswordRequest resetPasswordRequest){

        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        memp.setPassword(resetPasswordRequest.getPassword());
        memp.encodePassword(passwordEncoder);

        return mempRepository.save(memp);
    }

}
