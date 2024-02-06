package com.example.smstest.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 비밀번호 수정 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequest {

    private String password;
    private String password_confirm;

    @Builder
    public ResetPasswordRequest(Long userId, String password, String password_confirm) {
        this.password = password;
        this.password_confirm = password_confirm;
    }
}