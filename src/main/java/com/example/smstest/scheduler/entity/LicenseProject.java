package com.example.smstest.scheduler.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@ToString
@Table(name = "license_project_code")
public class LicenseProject {
    @Id
    @Column(name = "PROJECT_GUID", length = 50)
    private String projectGuid;

    @ManyToOne
    @JoinColumn(name = "COMPANY_GUID", referencedColumnName = "COMPANY_GUID", insertable = false, updatable = false)
    private LicenseCompany company;

    @Column(name = "AUTOCOMMAND_GUID", length = 50)
    private String autoCommandGuid;

    @Column(name = "PROJECT_NAME", length = 255)
    private String projectName;

    @Column(name = "PROJECT_CODE", length = 20)
    private String projectCode;

    @Column(name = "PROJECT_REGDATE")
    private Date projectRegDate;

}
