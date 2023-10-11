package com.example.smstest.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/task")
public class TaskController {


    private final TaskRepository taskRepository;

    @PostMapping("/updateTask/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task updatedTaskData) {
        Task taskToUpdate = taskRepository.findById(taskId).orElse(null);

        if (taskToUpdate != null) {
            taskToUpdate.setCategory(updatedTaskData.getCategory());
            taskToUpdate.setTaskName(updatedTaskData.getTaskName());
            taskToUpdate.setEstimatedStartDate(updatedTaskData.getEstimatedStartDate());

            taskRepository.save(taskToUpdate);
        }
        return taskToUpdate;
    }
}
