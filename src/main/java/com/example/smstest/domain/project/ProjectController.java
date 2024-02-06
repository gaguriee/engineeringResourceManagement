package com.example.smstest.domain.project;


import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.project.dto.ProjectRequest;
import com.example.smstest.domain.project.entity.DeliveryEquipment;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.DeliveryEquipmentRepository;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.support.repository.support.SupportRepository;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.entity.TaskCategory;
import com.example.smstest.domain.task.repository.TaskCategoryRepository;
import com.example.smstest.domain.task.repository.TaskRepository;
import com.example.smstest.external.license.LicenseProject;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
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
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final MempRepository mempRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final DeliveryEquipmentRepository deliveryEquipmentRepository;
    private final TeamRepository teamRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * [ 프로젝트 현황 ]
     * 프로젝트 리스트 가져오기
     *
     * @param Keyword
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/search")
    public String searchProject(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("currentPage", pageable.getPageNumber());

        Page<Project> projects = projectService.searchProjects(Keyword, pageable);
        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", projects.getTotalPages());

        return "projectBoard";
    }

    /**
     * [ 지원내역 등록 내 ]
     * 라이센스 프로젝트 리스트 가져오기
     *
     * @param Keyword
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/select")
    public String selectLicenseProject(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("currentPage", pageable.getPageNumber());

        Page<LicenseProject> projects = projectService.searchLicenseProjects(Keyword, pageable);

        model.addAttribute("projects", projects);
        model.addAttribute("totalPages", projects.getTotalPages());

        return "projectSelect";
    }

    /**
     * [ 프로젝트 디테일 ]
     * 프로젝트 디테일 뷰 반환
     * - 프로젝트 정보
     * - 해당 프로젝트 관련 일정 리스트
     * - 해당 프로젝트 관련 지원내역 Pageable 객체
     *
     * @param projectId
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/{projectId}")
    public String viewProjectDetail(
            @PathVariable Long projectId,
            @PageableDefault(size = 15) Pageable pageable,
            Model model) {

        Memp user = mempRepository.findFirstByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("user", user);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));
        model.addAttribute("project", project);

        Page<Support> supports = supportRepository.findAllByProjectIdOrderBySupportDateDesc(projectId, pageable);
        model.addAttribute("supports", supports);
        model.addAttribute("totalPages", supports.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());

        List<Task> tasks = taskRepository.findAllByProjectIdOrderByEstimatedStartDateAsc(projectId);
        model.addAttribute("tasks", tasks);

        List<TaskCategory> taskCategories = taskCategoryRepository.findAll();
        model.addAttribute("taskCategories", taskCategories);

        // 프로젝트 수정을 위한 DTO 객체 생성
        ProjectRequest projectRequest = ProjectRequest.builder()
                .projectId(projectId)
                .engineerId(Optional.ofNullable(project.getEngineer()).map(Memp::getId).orElse(null))
                .subEngineerId(Optional.ofNullable(project.getSubEngineer()).map(Memp::getId).orElse(null))
                .teamId(Optional.ofNullable(project.getTeam()).map(Team::getId).orElse(null))
                .productId(Optional.ofNullable(project.getProduct()).map(Product::getId).orElse(null))
                .orderAmount(project.getOrderAmount())
                .deliveryEquipmentList(deliveryEquipmentRepository.findAllByProjectId(project.getId()))
                .build();

        projectRequest.setProjectId(project.getId());

        List<Client> clients = clientRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);
        model.addAttribute("clients", clients);
        model.addAttribute("products", products);
        model.addAttribute("teams", teams);
        model.addAttribute("projectRequest", projectRequest);

        return "projectDetails";
    }

    /**
     * [ 프로젝트 수정 ]
     *
     * @param projectRequest
     * @param model
     * @return
     */
    @PostMapping("/modify")
    @Transactional
    public String modifyProject(ProjectRequest projectRequest, Model model) {

        Memp user = mempRepository.findFirstByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("user", user);

        Project newProject = projectRepository.findById(projectRequest.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));


        newProject.updateProject(
                productRepository.findById(projectRequest.getProductId()).get(),
                teamRepository.findById(projectRequest.getTeamId()).get(),
                mempRepository.findById(projectRequest.getEngineerId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)),
                projectRequest.getSubEngineerId() == null ? null :
                        mempRepository.findById(projectRequest.getSubEngineerId()).orElse(null),
                projectRequest.getOrderAmount()
        );

        newProject.setDeliveryEquipmentList(null);
        projectRepository.save(newProject);

        deliveryEquipmentRepository.deleteAllByProject(newProject);

        List<DeliveryEquipment> deliveryEquipmentRequests = projectRequest.getDeliveryEquipmentList().stream()
                .map(DeliveryEquipment::of)
                .filter(equipment -> equipment.getEquipmentName() != null && equipment.getQuantity() != null)
                .peek(equipment -> {
                    equipment.setProject(newProject);
                })
                .collect(Collectors.toList());

        deliveryEquipmentRepository.saveAll(deliveryEquipmentRequests);

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

        return "redirect:/project/" + projectRequest.getProjectId();
    }

}
