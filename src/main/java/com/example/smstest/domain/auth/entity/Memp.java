package com.example.smstest.domain.auth.entity;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.team.entity.Team;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "memp")
public class Memp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memp_id")
    private Long id;

    @Column(name = "이름", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "직책", nullable = false)
    private String position;

    @Column(name = "직급", nullable = false)
    private String rank;

    @Column(name = "캘린더색상", nullable = false)
    private String calenderColor;

    @Column(unique =true)
    private String username;

    @NotNull
    private String password;

    public Memp(AccountRequest accountRequest){
        this.username= accountRequest.getUsername();
        this.password= accountRequest.getPassword();
    }

    @Builder
    public Memp(String username, @NotNull String password){
        this.username=username;
        this.password=password;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
