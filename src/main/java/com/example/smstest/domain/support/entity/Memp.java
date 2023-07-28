package com.example.smstest.domain.support.entity;

import javax.persistence.*;

@Entity
@Table(name = "memp")
public class Memp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memp_id")
    private Long id;

    @Column(name = "이름", nullable = false)
    private String name;

    @Column(name = "팀명", nullable = false)
    private String teamName;

    @Column(name = "직책", nullable = false)
    private String position;

    @Column(name = "직급", nullable = false)
    private String rank;

    // Constructors, getters, setters, and other properties
}
