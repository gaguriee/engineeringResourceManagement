package com.example.smstest.domain.task.Interface;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface TaskService {
    String deleteTask(Long taskId);
    String updateTask(Long taskId, Map<String, Object> json,
                      MultipartFile file);

    String saveTask(Long taskId, Map<String, Object> json,
                     MultipartFile file);
}
