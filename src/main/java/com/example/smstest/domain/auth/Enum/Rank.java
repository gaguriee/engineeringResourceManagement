package com.example.smstest.domain.auth.Enum;

import lombok.Getter;

@Getter
public enum Rank {
    SENIOR("수석"),
    MANAGER("책임"),
    SENIOR_MANAGER("선임"),
    JUNIOR_MANAGER("주임"),
    ENGINEER("엔지니어"),
    INTERN("인턴");

    private final String label;

    Rank(String label) {
        this.label = label;
    }

}