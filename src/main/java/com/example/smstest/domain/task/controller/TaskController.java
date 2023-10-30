package com.example.smstest.domain.task.controller;

import com.example.smstest.domain.task.Interface.TaskService;
import com.example.smstest.domain.task.dto.TaskRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDTO updatedTask) {
        return taskService.updateTask(taskId, updatedTask);
    }

    @PostMapping("/save/{projectId}")
    @ResponseBody
    public String saveTasks(@PathVariable Long projectId, @RequestBody List<TaskRequestDTO> taskList) {
        return taskService.saveTasks(projectId, taskList);
    }
}
