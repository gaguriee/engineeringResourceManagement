package com.example.smstest.domain.support.entity;

import javax.persistence.*;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "팀_id")
    private Long id;

    @Column(name = "팀명", nullable = false)
    private String name;

    // Constructors, getters, setters, and other properties
}
