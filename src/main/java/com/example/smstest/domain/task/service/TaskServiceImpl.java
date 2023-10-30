package com.example.smstest.domain.task.service;

import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.task.Interface.TaskService;
import com.example.smstest.domain.task.dto.TaskRequestDTO;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.repository.TaskCategoryRepository;
import com.example.smstest.domain.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskCategoryRepository taskCategoryRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.taskCategoryRepository = taskCategoryRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public String deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            taskRepository.delete(task);
            return "Task deleted!";
        }
        return "Task not found!";
    }

    @Override
    public String updateTask(Long taskId, TaskRequestDTO updatedTask) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            return setTaskRequestDto(updatedTask, task);
        }
        return "Task not found!";
    }

    @Override
    public String saveTasks(Long projectId, List<TaskRequestDTO> taskList) {
        for (TaskRequestDTO taskRequest : taskList) {
            Task task = new Task();
            task.setProject(projectRepository.findById(projectId).get());
            task.setCategory(taskCategoryRepository.findById(taskRequest.getCategoryId()).get());
            task.setTaskName(taskRequest.getTaskName());
            task.setEstimatedStartDate(taskRequest.getEstimatedStartDate());
            task.setEstimatedEndDate(taskRequest.getEstimatedEndDate());
            task.setActualStartDate(taskRequest.getActualStartDate());
            task.setActualEndDate(taskRequest.getActualEndDate());
            task.setActualOutput(taskRequest.getActualOutput());
            taskRepository.save(task);
        }
        return "작업 목록이 성공적으로 저장되었습니다.";
    }

    private String setTaskRequestDto(TaskRequestDTO updatedTask, Task task) {
        task.setCategory(taskCategoryRepository.findById(updatedTask.getCategoryId()).get());
        task.setTaskName(updatedTask.getTaskName());
        task.setEstimatedStartDate(updatedTask.getEstimatedStartDate());
        task.setEstimatedEndDate(updatedTask.getEstimatedEndDate());
        task.setActualStartDate(updatedTask.getActualStartDate());
        task.setActualEndDate(updatedTask.getActualEndDate());
        task.setActualOutput(updatedTask.getActualOutput());
        taskRepository.save(task);
        if (task.getProgress() != null)
            return task.getProgress().toString();
        else
            return "";
    }
}
