package com.example.smstest.domain.support.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "고객사_id")
    private Integer id;

    @Column(name = "고객사", nullable = false)
    private String name;

    @Column(name = "프로젝트명", nullable = true)
    private String project;
}