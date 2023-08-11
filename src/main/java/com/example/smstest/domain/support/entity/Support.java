package com.example.smstest.domain.support.entity;
import lombok.Data;

import javax.persistence.*;

import java.util.Date;

@Entity
@Data
@Table(name = "support")
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_id")
    private Long id;

    @Column(name = "고객담당자")
    private String customerContact;

    @Column(name = "지원일자")
    @Temporal(TemporalType.DATE)
    private Date supportDate;

    @Column(name = "레드마인_일감")
    private String redmineIssue;

    @Column(name = "작업제목")
    private String taskTitle;

    @Column(name = "작업요약", columnDefinition = "text")
    private String taskSummary;

    @Column(name = "작업세부내역", columnDefinition = "text")
    private String taskDetails;

    @ManyToOne
    @JoinColumn(name = "고객사_id")
    private Customer customer;

    @Column(name = "고객사")
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "팀_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "제품_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "이슈_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "상태_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "엔지니어_id")
    private Memp engineer;

    @Column(name = "부엔지니어")
    private String subEngineerName;

    @ManyToOne
    @JoinColumn(name = "지원형태_id")
    private SupportType supportType;

    @Column(name = "지원형태_시간")
    private Integer supportTypeHour;

}
