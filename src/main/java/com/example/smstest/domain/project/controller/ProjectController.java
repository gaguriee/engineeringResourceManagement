package com.example.smstest.domain.project.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.Interface.ProjectService;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.TeamRepository;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;
    private final ProjectRepository projectRepository;

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

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Project> projects = projectService.searchProjects(Keyword, pageable);
        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("projects", projects);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);
        model.addAttribute("totalPages", projects.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        return "projectBoard";
    }

    @GetMapping("/detail")
    public String viewProjectDetail(
            @RequestParam(required = false) Long projectId,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);

        Project project = projectRepository.findById(projectId).get();
        model.addAttribute("project", project);

        return "projectDetails";
    }

    @GetMapping("/select")
    public String selectProject(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Project> projects = projectService.searchProjects(Keyword, pageable);
        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("projects", projects);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);
        model.addAttribute("totalPages", projects.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        return "projectSearch";
    }

}
