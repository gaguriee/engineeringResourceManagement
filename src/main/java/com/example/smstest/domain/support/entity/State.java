package com.example.smstest.domain.support.entity;

import javax.persistence.*;

@Entity
@Table(name = "state")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상태_id")
    private Long id;

    @Column(name = "상태", nullable = false)
    private String name;

    // Constructors, getters, setters, and other properties
}
