package com.example.smstest.domain.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 JPA를 사용하여 데이터베이스에 저장되는 권한(Authority) 엔티티 클래스
 */

@Entity
@Table(name = "authority")
@Data
@NoArgsConstructor
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    public static Authority of(String authorityName) {
        Authority authority = new Authority();
        authority.authorityName = authorityName;
        return authority;
    }
}