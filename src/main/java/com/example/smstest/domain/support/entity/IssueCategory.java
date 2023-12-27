package com.example.smstest.domain.support.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
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

    @Column(name = "visibility")
    private Boolean visibility;

    @Column(name = "division_id", nullable = false)
    private Integer divisionId;

    /**
     * 순환 참조 문제로 만든 별도의 toString 메소드
     */
    public String toString() {
        return "IssueCategory{" +
                "Id=" + id +
                ", Name='" + name + '\'' +
                '}';
    }
}