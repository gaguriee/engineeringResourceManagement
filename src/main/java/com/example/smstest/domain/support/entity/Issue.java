package com.example.smstest.domain.support.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "issue")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "이슈_id")
    private Long id;

    @Column(name = "이슈", nullable = false)
    private String name;

    @Column(name = "참조")
    private String description;

    // Constructors, getters, setters, and other properties
}