package com.example.smstest.domain.support.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "issue_대분류")
public class IssueCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "이슈_id")
    private Long id;

    @Column(name = "이슈", nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "대분류", fetch = FetchType.EAGER) // 즉시 로딩으로 변경
    private List<Issue> issues;

    @Column(name = "priority")
    private Integer priority;
}