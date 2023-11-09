package com.example.smstest.domain.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileEditorDto {

    private String uploadPath ;
    private String filename;
    private String filePath;
    private Long size;

}