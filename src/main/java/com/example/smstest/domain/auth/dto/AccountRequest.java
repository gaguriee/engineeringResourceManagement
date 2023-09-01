package com.example.smstest.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountRequest {

    private String name;

    private String Position;

    private String Rank;

    private Integer teamId;

    private String username;

    private String password;

    private String password_confirm;


    @Builder
    public AccountRequest(String name, String Position, String Rank, Integer teamId, String username, String password, String password_confirm) {
        this.name = name;
        this.Position = Position;
        this.Rank = Rank;
        this.teamId = teamId;
        this.username = username;
        this.password = password;
        this.password_confirm = password_confirm;
    }
}