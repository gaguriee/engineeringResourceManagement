package com.example.smstest.domain.client.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 고객사 Entity
 */
@Entity
@Data
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "고객사_id")
    private Integer id;

    @Column(name = "고객사", nullable = false)
    private String name;

    @Column(name = "company_regdate")
    private Date companyRegDate;

    @Column(name = "company_guid", length = 50)
    private String companyGuid;


}