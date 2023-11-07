package com.example.smstest.domain.file;

import lombok.*;

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

    public File toEntity() {
        File build = File.builder()
                .id(id)
                .supportId(supportId)
                .taskId(taskId)
                .origFilename(origFilename)
                .filename(filename)
                .filePath(filePath)
                .size(size)
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