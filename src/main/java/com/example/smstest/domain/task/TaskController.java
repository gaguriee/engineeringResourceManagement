package com.example.smstest.domain.task;

import com.example.smstest.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/task")
public class TaskController {


    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ProjectRepository projectRepository;

    @PostMapping("/delete/{taskId}")
    @ResponseBody
    public String deleteTask(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            taskRepository.delete(task);
            return "Task deleted!";
        }
        return "Task not found!";
    }

    @PostMapping("/update/{taskId}")
    @ResponseBody
    public String updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto updatedTask) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            return setTaskRequestDto(updatedTask, task);
        }
        return "Task not found!";
    }


    @PostMapping("/save/{projectId}")
    @ResponseBody
    public String saveTasks(@PathVariable Long projectId, @RequestBody List<TaskRequestDto> taskList) {
        // 클라이언트에서 전송한 작업 목록을 저장할 비즈니스 로직을 작성
        for (TaskRequestDto taskRequest : taskList) {
            Task task = new Task();

            // TaskRequestDto에서 Task로 데이터 복사
            task.setProject(projectRepository.findById(projectId).get());
            task.setCategory(taskCategoryRepository.findById(taskRequest.getCategoryId()).get());
            task.setTaskName(taskRequest.getTaskName());
            task.setEstimatedStartDate(taskRequest.getEstimatedStartDate());
            task.setEstimatedEndDate(taskRequest.getEstimatedEndDate());
            task.setActualStartDate(taskRequest.getActualStartDate());
            task.setActualEndDate(taskRequest.getActualEndDate());
            task.setActualOutput(taskRequest.getActualOutput());

            // 데이터베이스에 저장
            taskRepository.save(task);
        }

        return "작업 목록이 성공적으로 저장되었습니다.";
    }

    private String setTaskRequestDto(@RequestBody TaskRequestDto updatedTask, Task task) {
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
