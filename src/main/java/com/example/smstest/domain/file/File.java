package com.example.smstest.domain.file;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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

    @JoinColumn(name = "support_id")
    private Long supportId;

    @JoinColumn(name = "task_id")
    private Long taskId;

    @Column(nullable = false)
    private String origFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long size;

    @Builder
    public File(Long id, Long supportId, Long taskId, String origFilename, String filename, String filePath, Long size) {
        this.id = id;
        this.supportId = supportId;
        this.taskId = taskId;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.size = size;
    }
}