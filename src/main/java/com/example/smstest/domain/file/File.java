package com.example.smstest.domain.file;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * File Table 엔티티
 */
@Getter
@Setter
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "support_id")
    private Long supportId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(nullable = false)
    private String origFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long size;

    /**
     * 파일이 저장된 ip를 저장해서, 같은 ip에서 저장된 객체들만 가져옴
     * (로컬, 개발, 운영 서버 등 저장된 위치 구분)
     */
    @Column(name = "saved_ip_address")
    private String savedIpAddress;

    @Builder
    public File(Long id, Long supportId, Long taskId, String origFilename, String filename, String filePath, Long size, String savedIpAddress) {
        this.id = id;
        this.supportId = supportId;
        this.taskId = taskId;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.size = size;
        this.savedIpAddress = savedIpAddress;
    }
}