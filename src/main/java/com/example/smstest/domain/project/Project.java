package com.example.smstest.domain.project;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.organization.entity.Team;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

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

    @ManyToOne
    @JoinColumn(name = "담당엔지니어_id")
    private Memp engineer;

    @ManyToOne
    @JoinColumn(name = "부담당엔지니어_id")
    private Memp subEngineer;

    @Column(name = "project_regdate")
    private Date projectRegDate;

    @Column(name = "project_guid", length = 50)
    private String projectGuid;


    @Column(name = "수주금액")
    private Integer orderAmount;

    @Column(name = "납품장비")
    private String equipment;

    @Builder
    public Project(String name, Client client, String uniqueCode, Product product, Team team, Memp engineer, Memp subEngineer, Date projectRegDate, String projectGuid,
                   Integer orderAmount, String equipment ) {
        this.name = name;
        this.client = client;
        this.uniqueCode = uniqueCode;
        this.product = product;
        this.team = team;
        this.engineer = engineer;
        this.subEngineer = subEngineer;
        this.projectRegDate = projectRegDate;
        this.projectGuid = projectGuid;
        this.orderAmount = orderAmount;
        this.equipment = equipment;
    }

    // 프로젝트 이름 변경 시 업데이트
    public void updateProject(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateProject(Product product, Team team, Memp engineer, Memp subEngineer, Integer orderAmount, String equipment) {

        if (product != null) {
            this.product = product;
        }
        if (team != null) {
            this.team = team;
        }
        if (engineer != null) {
            this.engineer = engineer;
        }
        this.subEngineer = subEngineer;
        this.orderAmount = orderAmount;
        this.equipment = equipment;

    }
}
