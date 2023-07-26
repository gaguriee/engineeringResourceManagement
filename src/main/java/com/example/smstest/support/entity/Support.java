package com.example.smstest.support.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "support", schema = "public") // Specify the table and schema if needed
public class Support {

    @Id
    @Column(name = "파일id", length = 100, nullable = false)
    private String 파일id;

    @Column(name = "제품명", length = 100)
    private String 제품명;

    @Column(name = "고객사", length = 100)
    private String 고객사;

    @Column(name = "고객담당자", length = 100)
    private String 고객담당자;

    @Column(name = "작업구분", length = 50)
    private String 작업구분;

    @Column(name = "이슈구분", length = 50)
    private String 이슈구분;

    @Column(name = "업무구분", length = 50)
    private String 업무구분;

    @Column(name = "담당엔지니어", length = 100)
    private String 담당엔지니어;

    @Column(name = "지원일자")
    private Date 지원일자;

    @Column(name = "지원형태", length = 50)
    private String 지원형태;

    @Column(name = "레드마인_일감", length = 50)
    private String 레드마인_일감;

    @Column(name = "작업제목", length = 200)
    private String 작업제목;

    @Column(name = "작업요약")
    private String 작업요약;

    @Column(name = "작업세부내역")
    private String 작업세부내역;

    @Column(name = "파일명", length = 200)
    private String 파일명;

    @Column(name = "위치", length = 200)
    private String 위치;


}
