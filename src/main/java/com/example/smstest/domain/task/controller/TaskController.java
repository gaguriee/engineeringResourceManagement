package com.example.smstest.domain.task.controller;

import com.example.smstest.domain.task.Interface.TaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * WBS 작업 일정에 관련된 Controller
 * 작업 일정 생성, 업데이트, 삭제
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/delete/{taskId}")
    @ResponseBody
    public String deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @PostMapping("/update/{taskId}")
    @ResponseBody
    public String updateTask(@PathVariable Long taskId,@RequestPart Map<String, Object> json,
                             @RequestPart(required = false) MultipartFile file) {
        return taskService.updateTask(taskId, json, file);
    }

    @PostMapping("/save/{projectId}")
    @ResponseBody
    public String saveTasks(@PathVariable Long projectId, @RequestPart Map<String, Object> json,
                            @RequestPart(required = false) MultipartFile file) {
        return taskService.saveTask(projectId, json, file);
    }

}
