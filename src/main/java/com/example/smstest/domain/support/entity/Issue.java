package com.example.smstest.domain.support.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "issue")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "이슈_id")
    private Long id;

    @Column(name = "이슈", nullable = false)
    private String name;

    @Column(name = "참조")
    private String description;

    @ManyToOne
    @JoinColumn(name = "대분류_이슈_id")
    private IssueCategory 대분류;

    @Column(name = "priority")
    private Integer priority;

    /**
     * 순환 참조 문제로 만든 별도의 toString 메소드
     */
    public String toString() {
        return "Issue{" +
                "Id=" + id +
                ", Name='" + name + '\'' +
                '}';
    }

}