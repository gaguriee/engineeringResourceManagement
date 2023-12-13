package com.example.smstest.external.employee.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 인사 연동 DB 내 소속 데이터 Entity
 */
@Entity
@Data
@Table(name = "tb_mdeptdata", schema = "ta_db")
public class Department {

    @Id
    @Column(name = "deptguid", nullable = false)
    private UUID deptguid;

    @Column(name = "deptname", nullable = false, length = 255)
    private String deptname;

    @Column(name = "parentdeptguid", nullable = false)
    private UUID parentdeptguid;

    @Column(name = "deptdepth", nullable = false)
    private int deptdepth;

}
