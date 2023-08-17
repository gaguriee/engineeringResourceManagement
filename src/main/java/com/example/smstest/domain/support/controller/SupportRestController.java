package com.example.smstest.domain.support.controller;

import com.example.smstest.domain.support.dto.PasswordComparisonRequest;
import com.example.smstest.domain.support.dto.PasswordMatchResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportRestController {

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/compare-password")
    public PasswordMatchResponse comparePassword(@RequestBody PasswordComparisonRequest request) {
        Support support = supportRepository.findById(request.getSupportId()).orElse(null);

        if (support != null) {
            boolean passwordMatch = passwordEncoder.matches(request.getEnteredPassword(), support.getPassword());
            return new PasswordMatchResponse(passwordMatch);
        }

        return new PasswordMatchResponse(false);
    }
}