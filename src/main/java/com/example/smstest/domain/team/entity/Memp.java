package com.example.smstest.domain.team.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "memp")
public class Memp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memp_id")
    private Long id;

    @Column(name = "이름", nullable = false)
    private String name;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "직책", nullable = false)
    private String position;

    @Column(name = "직급", nullable = false)
    private String rank;

    @Column(name = "캘린더색상", nullable = false)
    private String calenderColor;

}
