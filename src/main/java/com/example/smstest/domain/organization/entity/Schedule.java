package com.example.smstest.domain.organization.entity;

import com.example.smstest.domain.auth.entity.Memp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    @Column(name = "일정_내용", columnDefinition = "text")
    private String content;

    @Column(name = "일정_날짜")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "팀_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "엔지니어_id")
    private Memp memp;

    @Builder
    public Schedule(String content, Date date, Team team, Memp memp) {
        this.content = content;
        this.date = date;
        this.team = team;
        this.memp = memp;
    }

}
