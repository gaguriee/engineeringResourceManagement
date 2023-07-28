package com.example.smstest.domain.support.entity;

import javax.persistence.*;

@Entity
@Table(name = "issue")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "이슈_id")
    private Long id;

    @Column(name = "이슈", nullable = false)
    private String name;

    // Constructors, getters, setters, and other properties
}