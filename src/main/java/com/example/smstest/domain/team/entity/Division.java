package com.example.smstest.domain.team.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "division")
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "본부_id")
    private Integer id;

    @Column(name = "본부", nullable = false)
    private String name;

}
