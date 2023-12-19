package com.example.smstest.domain.auth.entity;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.organization.entity.Team;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;


/**
 * 사용자를 저장하는 Memp 엔티티 클래스
 */
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "직책", nullable = false)
    private String position;

    @Column(name = "직급", nullable = false)
    private String rank;

    @Column(name = "캘린더색상", nullable = true)
    private String calenderColor;

    @Column(unique =true)
    private String username;

    @ToString.Exclude
    @NotNull
    private String password;

    @Column(name = "active")
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = {
                    @JoinColumn(name = "memp_id", referencedColumnName = "memp_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "authority_name", referencedColumnName = "authority_name")
            }
    )
    @ToString.Exclude
    private Set<Authority> authorities;

    @Column(name = "last_login_at")
    private Timestamp lastLoginAt;


    public Memp(AccountRequest accountRequest){
        this.username= accountRequest.getUsername();
        this.password= accountRequest.getPassword();
    }

    @Builder
    public Memp(String name, Team team, String position, String rank, String calenderColor, String username, @NotNull String password, Set<Authority> authorities, boolean active) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.rank = rank;
        this.calenderColor = calenderColor;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }

    /**
     * 패스워드 인코딩
     * @param passwordEncoder
     */
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
