package com.example.smstest.domain.team.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "소속_id")
    private Integer id;

    @Column(name = "소속", nullable = false)
    private String name;

}
