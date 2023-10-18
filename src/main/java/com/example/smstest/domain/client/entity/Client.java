package com.example.smstest.domain.client.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "고객사_id")
    private Integer id;

    @Column(name = "고객사", nullable = false)
    private String name;


}