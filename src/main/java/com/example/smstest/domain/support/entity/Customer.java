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
    private Long id;

    @Column(name = "고객사", nullable = false)
    private String name;

    // Constructors, getters, setters, and other properties
}