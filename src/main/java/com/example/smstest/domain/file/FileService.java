package com.example.smstest.domain.file;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.UnknownHostException;


/**
 * 파일 등록 Service
 */
@Service
public class FileService {
    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * 파일 저장
     * @param fileDto
     * @return
     * @throws UnknownHostException
     */
    @Transactional // 이 어노테이션이 붙은 메소드에서만 DB 변경 가능
    public File saveFile(FileDto fileDto) throws UnknownHostException {
        return fileRepository.save(fileDto.toEntity());
    }

    /**
     * 일정 삭제 시 해당 일정에 첨부된 파일 삭제
     * @param taskId
     */
    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        fileRepository.deleteAllByTaskId(taskId);
    }

    /**
     * 파일 가져오기
     * @param id
     * @return FileDto
     */
    @Transactional
    public FileDto getFile(Long id) {
        File file = fileRepository.findById(id).get();

        return FileDto.builder()
                .id(id)
                .origFilename(file.getOrigFilename())
                .filename(file.getFilename())
                .filePath(file.getFilePath())
                .build();
    }
}