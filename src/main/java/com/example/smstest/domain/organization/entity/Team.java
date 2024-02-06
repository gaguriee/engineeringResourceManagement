package com.example.smstest.domain.organization.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 3. 팀 Entity (N팀, B팀 등)
 */
@Entity
@Data
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "팀_id")
    private Integer id;

    @Column(name = "팀명", nullable = false)
    private String name;

    @ManyToOne(optional = true)
    @JoinColumn(name = "소속_id")
    private Department department;

    @Column(name = "color")
    private String color;

}
