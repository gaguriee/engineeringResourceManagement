package com.example.smstest.domain.support.file;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDto {
    private Long id;
    private Long supportId;
    private String origFilename;
    private String filename;
    private String filePath;

    public File toEntity() {
        File build = File.builder()
                .id(id)
                .supportId(supportId)
                .origFilename(origFilename)
                .filename(filename)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public FileDto(Long id, Long supportId, String origFilename, String filename, String filePath) {
        this.id = id;
        this.supportId = supportId;
        this.origFilename = origFilename;
        this.filename = filename;
        this.filePath = filePath;
    }
}