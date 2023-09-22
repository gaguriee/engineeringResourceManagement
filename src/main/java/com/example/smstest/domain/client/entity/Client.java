package com.example.smstest.domain.client.entity;

import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.support.entity.Support;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

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