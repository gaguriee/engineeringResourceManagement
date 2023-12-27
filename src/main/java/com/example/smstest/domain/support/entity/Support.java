package com.example.smstest.domain.support.entity;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.file.File;
import com.example.smstest.domain.project.Project;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
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

    @Column(name = "created_at", nullable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "supportId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<File> files;

    /**
     * 파일이 저장된 ip를 저장해서, 같은 ip에서 저장된 객체들만 가져옴
     * (로컬, 개발, 운영 서버 등 저장된 위치 구분)
     */
    public Set<File> getFiles() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        InetAddress finalLocalhost = localhost;

        if (files == null){
            return null;
        }

        return files.stream()
                .filter(file -> file.getSavedIpAddress().equals(finalLocalhost.getHostAddress())) // 저장 ip와 현 ip가 동일한 파일만 가져옴
                .collect(Collectors.toSet());
    }
}
