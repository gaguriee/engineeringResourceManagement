package com.example.smstest.domain.support.dto;

import lombok.Getter;

@Getter
public class PasswordMatchResponse {
    private boolean passwordMatch;

    public PasswordMatchResponse(boolean passwordMatch) {
        this.passwordMatch = passwordMatch;
    }

    public void setPasswordMatch(boolean passwordMatch) {
        this.passwordMatch = passwordMatch;
    }
}