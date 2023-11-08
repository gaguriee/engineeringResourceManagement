package com.example.smstest.domain.file;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.UnknownHostException;

@Service
public class FileService {
    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public File saveFile(FileDto fileDto) throws UnknownHostException {
        return fileRepository.save(fileDto.toEntity());
    }

    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        fileRepository.deleteAllByTaskId(taskId);
    }

    @Transactional
    public FileDto getFile(Long id) {
        File file = fileRepository.findById(id).get();

        FileDto fileDto = FileDto.builder()
                .id(id)
                .origFilename(file.getOrigFilename())
                .filename(file.getFilename())
                .filePath(file.getFilePath())
                .build();
        return fileDto;
    }
}