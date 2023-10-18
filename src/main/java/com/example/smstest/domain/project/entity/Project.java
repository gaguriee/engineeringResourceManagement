package com.example.smstest.domain.project.entity;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.team.entity.Team;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
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

    @Column(name = "고유코드")
    private String uniqueCode;

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
    public Project(String name, Client client, String uniqueCode, Product product, Team team, Date startDate, Date finishDate, Memp engineer, Memp subEngineer) {
        this.name = name;
        this.client = client;
        this.uniqueCode = uniqueCode;
        this.product = product;
        this.team = team;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.engineer = engineer;
        this.subEngineer = subEngineer;
    }

    public void updateProject(String name, Client client, String uniqueCode, Product product, Team team, Date startDate, Date finishDate, Memp engineer, Memp subEngineer) {
        if (name != null) {
            this.name = name;
        }
        if (client != null) {
            this.client = client;
        }
        if (uniqueCode != null) {
            this.uniqueCode = uniqueCode;
        }
        if (product != null) {
            this.product = product;
        }
        if (team != null) {
            this.team = team;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (finishDate != null) {
            this.finishDate = finishDate;
        }
        if (engineer != null) {
            this.engineer = engineer;
        }
        if (subEngineer != null) {
            this.subEngineer = subEngineer;
        }
    }

    public void updateProject(String name, Client client,  Date startDate, Date finishDate) {
        if (name != null) {
            this.name = name;
        }
        if (client != null) {
            this.client = client;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (finishDate != null) {
            this.finishDate = finishDate;
        }
    }
}
