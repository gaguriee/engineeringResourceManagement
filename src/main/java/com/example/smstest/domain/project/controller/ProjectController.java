package com.example.smstest.domain.project.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.project.service.ProjectService;
import com.example.smstest.domain.support.dto.ProjectRequest;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.entity.TaskCategory;
import com.example.smstest.domain.task.repository.TaskCategoryRepository;
import com.example.smstest.domain.task.repository.TaskRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import com.example.smstest.license.entity.LicenseProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * WBS (Project 별 지원 내역 및 일정 조회) 페이지 매핑 Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final SupportRepository supportRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    @GetMapping("/search")
    public String searchProject(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        getProjectList(Keyword, pageable, model);

        Page<Project> projects = projectService.searchProjects(Keyword, pageable);
        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", projects.getTotalPages());

        return "projectBoard";
    }

    @GetMapping("/detail")
    public String viewProjectDetail(
            @RequestParam(required = false, defaultValue = "false") Boolean supportHistory,
            @RequestParam(required = false) Long projectId,
            @PageableDefault(size = 15) Pageable pageable,
            Model model) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("user", user);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));
        model.addAttribute("project", project);

        Page<Support> supports = supportRepository.findAllByProjectIdOrderBySupportDateDesc(projectId, pageable);
        model.addAttribute("supports", supports);
        model.addAttribute("totalPages", supports.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());

        model.addAttribute("project", project);

        List<Task> tasks = taskRepository.findAllByProjectIdOrderByEstimatedStartDateAsc(projectId);
        model.addAttribute("tasks", tasks);

        List<TaskCategory> taskCategories = taskCategoryRepository.findAll();
        model.addAttribute("taskCategories", taskCategories);

        ProjectRequest projectRequest = ProjectRequest.builder()
                .engineerName(Optional.ofNullable(project.getEngineer()).map(Memp::getName).orElse(null))
                .teamId(Optional.ofNullable(project.getTeam()).map(Team::getId).orElse(null))
                .productId(Optional.ofNullable(project.getProduct()).map(Product::getId).orElse(null))
                .subEngineerName(Optional.ofNullable(project.getSubEngineer()).map(Memp::getName).orElse(null))
                .build();

        projectRequest.setProjectId(project.getId());

        model.addAttribute("projectRequest", projectRequest);


        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);
        model.addAttribute("supportHistory", supportHistory);

        return "projectDetails";
    }

    @PostMapping("/modify")
    public String modifyProject(
            ProjectRequest projectRequest, BindingResult bindingResult,
            Model model) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("user", user);

        Project newProject = projectRepository.findById(projectRequest.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));


        newProject.updateProject(
                productRepository.findById(projectRequest.getProductId()).get(),
                teamRepository.findById(projectRequest.getTeamId()).get(),
                mempRepository.findOneByName(projectRequest.getEngineerName())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)),
                mempRepository.findOneByName(projectRequest.getSubEngineerName())
                        .orElse(null)
        );

        projectRepository.save(newProject);

        model.addAttribute("project", newProject);

        List<Task> tasks = taskRepository.findAllByProjectIdOrderByEstimatedStartDateAsc(projectRequest.getProjectId());
        model.addAttribute("tasks", tasks);

        List<TaskCategory> taskCategories = taskCategoryRepository.findAll();
        model.addAttribute("taskCategories", taskCategories);

        model.addAttribute("projectRequest", projectRequest);

        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);

        return "redirect:/project/detail?projectId="+projectRequest.getProjectId();
    }


    @GetMapping("/select")
    public String selectProject(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        getProjectList(Keyword, pageable, model);
        Page<LicenseProject> projects = projectService.searchLicenseProjects(Keyword, pageable);

        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", projects.getTotalPages());

        return "projectSelect";
    }

    private void getProjectList(@RequestParam(required = false) String Keyword, @PageableDefault(size = 30) Pageable pageable, Model model) {
        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);
        model.addAttribute("currentPage", pageable.getPageNumber());
    }

}
