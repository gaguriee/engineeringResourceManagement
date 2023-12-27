package com.example.smstest.domain.support.entity;

import lombok.Getter;

import javax.persistence.*;


@Entity
@Getter
@Table(name = "support_type")
public class SupportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "지원상태_id")
    private Long id;

    @Column(name = "지원상태", nullable = false)
    private String name;

    @Column(name = "캘린더색상", nullable = false)
    private String calenderColor;
}