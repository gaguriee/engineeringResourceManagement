package com.example.smstest.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountRequest {

    private Long userId;

    private String username;

    private String password;

    private String password_confirm;

    @Builder
    public AccountRequest(String username, String password, String password_confirm){
        this.username = username;
        this.password = password;
        this.password_confirm = password_confirm;
    }
}