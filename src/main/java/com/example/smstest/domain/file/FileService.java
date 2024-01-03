package com.example.smstest.domain.file;

import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.UnknownHostException;
import java.util.Optional;


/**
 * 파일 등록 Service
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    /**
     * [ 파일 저장 ]
     * @param fileDto
     * @return
     * @throws UnknownHostException
     */
    @Transactional // 이 어노테이션이 붙은 메소드에서만 DB 변경 가능
    public File saveFile(FileDto fileDto) throws UnknownHostException {
        return fileRepository.save(fileDto.toEntity());
    }

    /**
     * [ 일정 삭제 시 해당 일정에 첨부된 파일 삭제 ]
     * @param taskId
     */
    @Transactional
    public void deleteAllByTaskId(Long taskId) {
        fileRepository.deleteAllByTaskId(taskId);
    }

    /**
     * [ 파일 가져오기 ]
     * @param id
     * @return FileDto
     */
    @Transactional
    public FileDto getFile(Long id) {
        Optional<File> file = fileRepository.findById(id);

        if (file.isPresent()){

            return FileDto.builder()
                    .id(id)
                    .origFilename(file.get().getOrigFilename())
                    .filename(file.get().getFilename())
                    .filePath(file.get().getFilePath())
                    .build();
        }

        else {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

    }
}