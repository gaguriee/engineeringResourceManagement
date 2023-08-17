package com.example.smstest.domain.support.dto;

public class PasswordMatchResponse {
    private boolean passwordMatch;

    public PasswordMatchResponse(boolean passwordMatch) {
        this.passwordMatch = passwordMatch;
    }

    public boolean isPasswordMatch() {
        return passwordMatch;
    }

    public void setPasswordMatch(boolean passwordMatch) {
        this.passwordMatch = passwordMatch;
    }
}