package com.example.smstest.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 수정 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ModifyUserinfoRequest {

    private String Position;

    private String Rank;

    private Integer teamId;

    private String password_confirm;


    @Builder
    public ModifyUserinfoRequest(String Position, String Rank, Integer teamId, String password_confirm) {
        this.Position = Position;
        this.Rank = Rank;
        this.teamId = teamId;
        this.password_confirm = password_confirm;
    }
}