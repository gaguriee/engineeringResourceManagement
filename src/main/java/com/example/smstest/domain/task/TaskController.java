package com.example.smstest.domain.task;

import com.example.smstest.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/task")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;




}