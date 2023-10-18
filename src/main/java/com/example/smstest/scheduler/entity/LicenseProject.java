package com.example.smstest.scheduler.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@ToString
@Table(name = "license_company_info")
public class LicenseProject {


    @Column(name = "company_guid")
    private String companyGuid;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "project_guid")
    private String projectGuid;

    @Id
    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "license_sdate")
    private Date licenseStartDate;

    @Column(name = "license_edate")
    private Date licenseEndDate;

    // 생성자, getter 및 setter 메서드 생략 (필요한 경우 추가)

    @Override
    public String toString() {
        return "LicenseCompanyInfo{" +
                "companyGuid='" + companyGuid + '\'' +
                ", companyName='" + companyName + '\'' +
                ", projectGuid='" + projectGuid + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", projectName='" + projectName + '\'' +
                ", licenseStartDate=" + licenseStartDate +
                ", licenseEndDate=" + licenseEndDate +
                '}';
    }
}

