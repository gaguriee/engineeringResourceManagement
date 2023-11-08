package com.example.smstest.domain.task.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * WBS 작업 분류 Entity [ 착수단계, 분석 및 설계 단계 등 ]
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "task_대분류")
public class TaskCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category")
    private String name;

}
