package com.example.smstest.domain.support.entity;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.file.File;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "support")
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_id")
    private Long id;

    @Column(name = "지원일자")
    @Temporal(TemporalType.DATE)
    private Date supportDate;

    @Column(name = "레드마인_일감")
    private String redmineIssue;

    @Column(name = "작업제목")
    private String taskTitle;

    @ToString.Exclude
    @Column(name = "작업요약", columnDefinition = "text")
    private String taskSummary;

    @ToString.Exclude
    @Column(name = "작업세부내역", columnDefinition = "text")
    private String taskDetails;

    @ManyToOne
    @JoinColumn(name = "프로젝트_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "제품_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "이슈_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "상태_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "엔지니어_id")
    private Memp engineer;

    @Column(name = "부엔지니어")
    private String subEngineerName;

    @ManyToOne
    @JoinColumn(name = "지원형태_id")
    private SupportType supportType;

    @Column(name = "지원형태_시간")
    private Float supportTypeHour;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "supportId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<File> files;

}
