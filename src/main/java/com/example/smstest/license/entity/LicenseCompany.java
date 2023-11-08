package com.example.smstest.license.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

/**
 * 라이센스 DB 내 고객사 Entity
 */
@Entity
@Getter
@Table(name = "license_company_code")
public class LicenseCompany {
    @Id
    @Column(name = "COMPANY_GUID", length = 50)
    private String companyGuid;

    @Column(name = "COMPANY_NAME", length = 255)
    private String companyName;

    @Column(name = "COMPANY_TYPE")
    private Integer companyType;

    @Column(name = "COMPANY_BUSINESSNO", length = 50)
    private String companyBusinessNo;

    @Column(name = "COMPANY_CEONAME", length = 50)
    private String companyCeoName;

    @Column(name = "COMPANY_ZIPCODE", length = 10)
    private String companyZipCode;

    @Column(name = "COMPANY_ADDR", length = 255)
    private String companyAddress;

    @Column(name = "COMPANY_PHONE", length = 100)
    private String companyPhone;

    @Column(name = "COMPANY_FAX", length = 100)
    private String companyFax;


    @Column(name = "COMPANY_REGDATE")
    private Date companyRegDate;

}