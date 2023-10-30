package com.example.smstest.domain.organization.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 1. 본부 Entity (기술 N본부, E본부 등)
 */
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
