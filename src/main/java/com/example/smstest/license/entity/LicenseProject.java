package com.example.smstest.license.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;

/**
 * 라이센스 DB 내 프로젝트 Entity
 */
@Entity
@Getter
@ToString
@Table(name = "license_project_code")
public class LicenseProject {
    @Id
    @Column(name = "PROJECT_GUID", length = 50)
    private String projectGuid;

    @ManyToOne
    @JoinColumn(name = "COMPANY_GUID", referencedColumnName = "COMPANY_GUID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
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
