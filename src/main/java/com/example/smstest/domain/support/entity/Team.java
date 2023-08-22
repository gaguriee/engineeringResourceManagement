package com.example.smstest.domain.support.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "팀_id")
    private Integer id;

    @Column(name = "팀명", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "소속_id")
    private Department department;
}
