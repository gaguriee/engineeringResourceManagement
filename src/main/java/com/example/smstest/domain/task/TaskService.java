package com.example.smstest.domain.task;

import com.example.smstest.domain.file.FileDto;
import com.example.smstest.domain.file.FileService;
import com.example.smstest.domain.file.MD5Generator;
import com.example.smstest.domain.project.ProjectRepository;
import com.example.smstest.domain.task.dto.TaskRequestDTO;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.repository.TaskCategoryRepository;
import com.example.smstest.domain.task.repository.TaskRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 일정 관련 Service
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ProjectRepository projectRepository;
    private final FileService fileService;

    /**
     * 일정 삭제
     * @param taskId
     * @return
     */
    public String deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        taskRepository.delete(task);
        return "Task deleted!";
    }

    /**
     * 기존 일정 업데이트
     * @param taskId
     * @param json
     * @param file
     * @return
     */
    public String updateTask(Long taskId, Map<String, Object> json,
                             MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        setTaskRequestDto(json, file, task);
        return "Task updated!";
    }

    /**
     * 신규 일정 등록
     * @param projectId
     * @param json
     * @param file
     * @return
     */
    public String saveTask(Long projectId, Map<String, Object> json,
                            MultipartFile file) {

        ObjectMapper mapper = new ObjectMapper();
        TaskRequestDTO taskRequest = mapper.convertValue(json,TaskRequestDTO.class);

        Task task = new Task();
        task.setProject(projectRepository.findById(projectId).get());
        task.setCategory(taskCategoryRepository.findById(taskRequest.getCategoryId()).get());
        task.setTaskName(taskRequest.getTaskName());
        task.setEstimatedStartDate(taskRequest.getEstimatedStartDate());
        task.setEstimatedEndDate(taskRequest.getEstimatedEndDate());
        task.setActualStartDate(taskRequest.getActualStartDate());
        task.setActualEndDate(taskRequest.getActualEndDate());
        Task newTask = taskRepository.save(task);

        return setTaskFile(file, newTask);
    }

    /**
     * json으로 받은 Request를 TaskRequestDto로 매핑
     * @param json
     * @param file
     * @param task
     * @return
     */
    private String setTaskRequestDto(Map<String, Object> json,
                                     MultipartFile file, Task task) {

        ObjectMapper mapper = new ObjectMapper();
        TaskRequestDTO updatedTask = mapper.convertValue(json,TaskRequestDTO.class);

        task.setCategory(taskCategoryRepository.findById(updatedTask.getCategoryId()).get());
        task.setTaskName(updatedTask.getTaskName());
        task.setEstimatedStartDate(updatedTask.getEstimatedStartDate());
        task.setEstimatedEndDate(updatedTask.getEstimatedEndDate());
        task.setActualStartDate(updatedTask.getActualStartDate());
        task.setActualEndDate(updatedTask.getActualEndDate());

        if (updatedTask.getFileDeleted()){
            task.setFiles(null);
            fileService.deleteAllByTaskId(task.getId());
        }

        Task newTask = taskRepository.save(task);

        return setTaskFile(file, newTask);
    }

    /**
     * 요청 들어온 file 저장
     * @param file
     * @param newTask
     * @return
     */
    private String setTaskFile(MultipartFile file, Task newTask){

        // 요청 들어온 file이 없을 경우 그냥 return
        if (file == null){
            return "작업 목록이 성공적으로 저장되었습니다.";
        }

        // file이 있을 경우 저장
        try {

            if (file.getOriginalFilename().isEmpty())
                throw new CustomException(ErrorCode.BAD_REQUEST);

            String origFilename = file.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();

            String savePath;
            String filePath;

            // OS 따라 구분자 분리
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")){
                savePath = System.getProperty("user.dir") + "\\files\\task";
                filePath = savePath + "\\" + filename;
            }
            else{
                savePath = System.getProperty("user.dir") + "/files/task";
                filePath = savePath + "/" + filename;
            }


            if (!new java.io.File(savePath).exists()) {
                try{
                    new java.io.File(savePath).mkdir();
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }

            file.transferTo(new java.io.File(filePath));
            
            // 이전에 등록되어 있던 File 삭제
            fileService.deleteAllByTaskId(newTask.getId());

            FileDto fileDto = new FileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setSize(file.getSize());
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);
            fileDto.setTaskId(newTask.getId());

            // 신규 등록 File 저장
            fileService.saveFile(fileDto);


            return "작업 목록이 성공적으로 저장되었습니다.";

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }
    }
}
