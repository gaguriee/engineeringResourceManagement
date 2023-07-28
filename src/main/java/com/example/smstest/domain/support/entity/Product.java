package com.example.smstest.domain.support.entity;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "제품_id")
    private Long id;

    @Column(name = "제품명", nullable = false)
    private String name;

    // Constructors, getters, setters, and other properties
}
