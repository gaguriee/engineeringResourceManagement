package com.example.smstest.domain.support.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "support", schema = "public") // Specify the table and schema if needed
public class Support {

    @Id
    @Column(name = "support_id", nullable = false)
    private Integer supportId;

    @Column(name = "파일id", length = 100, nullable = false)
    private String fileId;

    @Column(name = "제품명", length = 100)
    private String product;

    @Column(name = "고객사", length = 100)
    private String customer;

    @Column(name = "고객담당자", length = 100)
    private String manager;

    @Column(name = "작업구분", length = 50)
    private String task;

    @Column(name = "이슈구분", length = 50)
    private String issue;

    @Column(name = "업무구분", length = 50)
    private String state;

    @Column(name = "담당엔지니어", length = 100)
    private String engineer;

    @Column(name = "지원일자")
    private Date supportAt;

    @Column(name = "지원형태", length = 50)
    private String type;

    @Column(name = "레드마인_일감", length = 50)
    private String redmine;

    @Column(name = "작업제목", length = 200)
    private String title;

    @Column(name = "작업요약")
    private String summary;

    @Column(name = "작업세부내역")
    private String details;

    @Column(name = "파일명", length = 200)
    private String fileName;

    @Column(name = "위치", length = 200)
    private String location;

    @Column(name = "소속", length = 100)
    private String team;


}
