package com.example.smstest.employee.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * 인사 연동 DB 내 유저 데이터 Entity
 */
@Entity
@Data
@Table(name = "tb_mempdata", schema = "ta_db")
public class Employee {

    @Id
    @Column(name = "empguid", nullable = false)
    private UUID empguid;

    @Column(name = "userid", nullable = false, length = 255)
    private String userid;

    @Column(name = "empname", nullable = false, length = 255)
    private String empname;

    @ManyToOne
    @JoinColumn(name = "deptguid", foreignKey = @ForeignKey(name = "fk_tb_mempda_deptguid"))
    private Department department;

}
