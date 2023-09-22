package com.example.smstest.domain.project.entity;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_generator")
    @SequenceGenerator(name = "project_generator", sequenceName = "project_프로젝트_id_seq", allocationSize = 1)
    @Column(name = "프로젝트_id")
    private Long id;

    @Column(name = "프로젝트명")
    private String name;

//    @Column(name = "만료일")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "고객사_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "제품_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "팀_id")
    private Team team;

    @Column(name = "시작일")
    @Temporal(TemporalType.DATE)
    private Date startDate;


    @Column(name = "종료일")
    @Temporal(TemporalType.DATE)
    private Date finishDate;

    @ManyToOne
    @JoinColumn(name = "담당엔지니어_id")
    private Memp engineer;

    @ManyToOne
    @JoinColumn(name = "부담당엔지니어_id")
    private Memp subEngineer;

    @Builder
    public Project(String name, Client client, Product product, Team team, Date startDate, Date finishDate, Memp engineer, Memp subEngineer) {
        this.name = name;
        this.client = client;
        this.product = product;
        this.team = team;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.engineer = engineer;
        this.subEngineer = subEngineer;
    }
}
