package com.example.smstest.domain.support.file;

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

    @Column(nullable = false)
    private String origFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    @Builder
    public File(Long id, Long supportId, String origFilename, String filename, String filePath) {
        this.id = id;
        this.supportId = supportId;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }
}