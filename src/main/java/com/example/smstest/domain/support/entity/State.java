package com.example.smstest.domain.support.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "state")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "상태_id")
    private Long id;

    @Column(name = "상태", nullable = false)
    private String name;

    @Column(name = "참조")
    private String description;

}
