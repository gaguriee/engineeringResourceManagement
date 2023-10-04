package com.example.smstest.domain.auth.Enum;

import lombok.Getter;

@Getter
public enum Position {
    TEAM_LEADER("팀장"),
    TEAM_MEMBER("팀원");

    private final String label;

    Position(String label) {
        this.label = label;
    }

}