package com.example.smstest.employee.entity;

import lombok.Data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
