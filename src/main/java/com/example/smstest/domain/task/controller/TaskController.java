package com.example.smstest.domain.task.controller;

import com.example.smstest.domain.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * WBS 작업 일정에 관련된 Controller
 * 작업 일정 생성, 업데이트, 삭제
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    /**
     * 일정 삭제
     * @param taskId
     * @return
     */
    @PostMapping("/delete/{taskId}")
    @ResponseBody
    public String deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    /**
     * 기존 일정 수정 (파일 첨부)
     * @param taskId
     * @param json
     * @param file
     * @return
     */
    @PostMapping("/update/{taskId}")
    @ResponseBody
    public String updateTask(@PathVariable Long taskId,@RequestPart Map<String, Object> json,
                             @RequestPart(required = false) MultipartFile file) {
        return taskService.updateTask(taskId, json, file);
    }

    /**
     * 신규 일정 등록 (파일 첨부)
     * @param projectId
     * @param json
     * @param file
     * @return
     */
    @PostMapping("/save/{projectId}")
    @ResponseBody
    public String saveTasks(@PathVariable Long projectId, @RequestPart Map<String, Object> json,
                            @RequestPart(required = false) MultipartFile file) {
        return taskService.saveTask(projectId, json, file);
    }

}
