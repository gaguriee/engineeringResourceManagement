package com.example.smstest.domain.file;

import lombok.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 파일 업로드 및 다운로드에 사용되는 File DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDto {
    private Long id;
    private Long supportId;
    private Long taskId;
    private String origFilename;
    private String filename;
    private String filePath;
    private Long size;

    public File toEntity() throws UnknownHostException {

        InetAddress localhost = InetAddress.getLocalHost();

        File build = File.builder()
                .id(id)
                .supportId(supportId)
                .taskId(taskId)
                .origFilename(origFilename)
                .filename(filename)
                .filePath(filePath)
                .size(size)
                .savedIpAddress(localhost.getHostAddress())
                .build();
        return build;
    }

    @Builder
    public FileDto(Long id, Long supportId, Long taskId, String origFilename, String filename, String filePath, Long size) {
        this.id = id;
        this.supportId = supportId;
        this.taskId = taskId;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.size = size;
    }
}