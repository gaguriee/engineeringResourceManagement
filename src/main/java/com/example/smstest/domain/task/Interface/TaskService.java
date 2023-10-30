package com.example.smstest.domain.task.Interface;

import com.example.smstest.domain.task.dto.TaskRequestDTO;

import java.util.List;

public interface TaskService {
    String deleteTask(Long taskId);
    String updateTask(Long taskId, TaskRequestDTO updatedTask);
    String saveTasks(Long projectId, List<TaskRequestDTO> taskList);
}
