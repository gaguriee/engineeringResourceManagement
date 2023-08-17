package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.*;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class SupportCRUDController {
    private final SupportService supportService;
    private final ProductRepository productRepository;
    private final IssueRepository issueRepository;
    private final CustomerRepository customerRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;

    private final TeamRepository teamRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final SupportRepository supportRepository;

    public SupportCRUDController(SupportService supportService, ProductRepository productRepository, IssueRepository issueRepository, CustomerRepository customerRepository, StateRepository stateRepository, MempRepository mempRepository, TeamRepository teamRepository, SupportTypeRepository supportTypeRepository, SupportRepository supportRepository) {
        this.supportService = supportService;
        this.productRepository = productRepository;
        this.issueRepository = issueRepository;
        this.customerRepository = customerRepository;
        this.stateRepository = stateRepository;
        this.mempRepository = mempRepository;
        this.teamRepository = teamRepository;
        this.supportTypeRepository = supportTypeRepository;
        this.supportRepository = supportRepository;
    }


    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 필터링
    @GetMapping("/search")
    public String searchSupportByFilters(@RequestParam(required = false) List<Long> customerId,
                                         @RequestParam(required = false) List<Integer> teamId,
                                         @RequestParam(required = false) List<Long> productId,
                                         @RequestParam(required = false) List<Long> issueId,
                                         @RequestParam(required = false) List<Long> stateId,
                                         @RequestParam(required = false) List<Long> engineerId,
                                         @RequestParam(required = false) String Keyword,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                                         @RequestParam(required = false, defaultValue = "desc")  String sortOrder, // 추가된 파라미터
                                         Pageable pageable,
                                         Model model) {
        SupportFilterCriteria criteria = new SupportFilterCriteria();
        criteria.setCustomerId(customerId);
        criteria.setTeamId(teamId);
        criteria.setProductId(productId);
        criteria.setIssueId(issueId);
        criteria.setStateId(stateId);
        criteria.setEngineerId(engineerId);
        criteria.setTaskKeyword(Keyword);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<Support> result = supportService.searchSupportByFilters(criteria, pageable, sortOrder);
        Page<SupportResponse> responsePage = new PageImpl<>(
                result.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements());
        model.addAttribute("posts", responsePage);
        System.out.println(responsePage.stream().findFirst());

        model.addAttribute("totalPages", result.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지

        // Product 엔티티
        List<Product> allProducts = productRepository.findAll();
        model.addAttribute("allProducts", allProducts);

        // Issue 엔티티
        List<Issue> allIssues = issueRepository.findAll();
        model.addAttribute("allIssues", allIssues);

        Map<Integer, List<Issue>> groupedIssues = new HashMap<>();
        for (Issue issue : allIssues) {
            groupedIssues.computeIfAbsent(issue.get대분류(), k -> new ArrayList<>()).add(issue);
        }
        model.addAttribute("groupedIssues", groupedIssues);

        // State 엔티티
        List<State> allStates = stateRepository.findAll();
        model.addAttribute("allStates", allStates);

        // Team 엔티티
        List<Team> allTeams = teamRepository.findAll();
        model.addAttribute("allTeams", allTeams);

        // Member 엔티티
        List<Memp> allMemps = mempRepository.findAll();
        model.addAttribute("allMemps", allMemps);

        model.addAttribute("sortOrder", sortOrder);

        // 필터링 대상 엔티티만 가져옴
        // Model에 선택된 products, issues, states 추가
        if (productId != null) {
            List<Product> selectedProducts = productRepository.findAllById(productId);
            model.addAttribute("selectedProducts", selectedProducts);
        } else {
            model.addAttribute("selectedProducts", new ArrayList<Product>());
        }

        if (issueId != null) {
            List<Issue> selectedIssues = issueRepository.findAllById(issueId);
            model.addAttribute("selectedIssues", selectedIssues);
        } else {
            model.addAttribute("selectedIssues", new ArrayList<Issue>());
        }

        if (stateId != null) {
            List<State> selectedStates = stateRepository.findAllById(stateId);
            model.addAttribute("selectedStates", selectedStates);
        } else {
            model.addAttribute("selectedStates", new ArrayList<State>());
        }

        if (teamId != null) {
            List<Team> selectedTeams = teamRepository.findAllById(teamId);
            model.addAttribute("selectedTeams", selectedTeams);
        } else {
            model.addAttribute("selectedTeams", new ArrayList<Team>());
        }

        if (engineerId != null) {
            List<Memp> selectedMemps = mempRepository.findAllById(engineerId);
            model.addAttribute("selectedMemps", selectedMemps);
        } else {
            model.addAttribute("selectedMemps", new ArrayList<Memp>());
        }

        if (startDate != null) {
            String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
            model.addAttribute("startDate", formattedStartDate);
        }
        if (endDate != null) {
            String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
            model.addAttribute("endDate", formattedEndDate);
        }
        return "board";
    }


    // 상세보기
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Long supportId, Model model) {

        SupportResponse supportResponse = supportService.getDetails(supportId);
        model.addAttribute("support", supportResponse);

        return "details";
    }

    // 등록하기
    @PostMapping("/post")
    public String createSupport(@ModelAttribute SupportRequest supportRequest, RedirectAttributes redirectAttributes) {

        SupportResponse supportResponse = supportService.createSupport(supportRequest);
//        redirectAttributes.addFlashAttribute("support", supportResponse);

        return "redirect:/details?supportId="+supportResponse.getId();
    }

    @GetMapping("/create")
    public String createView(Model model) {

        List<Customer> customers = customerRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();

        model.addAttribute("customers", customers);
        model.addAttribute("issues", issues);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "create";
    }


    // 수정

    @GetMapping("/modify")
    public String modifyView(@RequestParam(required = false) Long supportId, Model model) {

        Support support = supportRepository.findById(supportId).get();
        model.addAttribute("support", support);

        List<Customer> customers = customerRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();

        model.addAttribute("customers", customers);
        model.addAttribute("issues", issues);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "modify";
    }
    @PostMapping("/modify")
    public String modifySupport(@ModelAttribute ModifyRequest modifyRequest, RedirectAttributes redirectAttributes) {

        SupportResponse supportResponse = supportService.modifySupport(modifyRequest);

        return "redirect:/details?supportId="+supportResponse.getId();
    }


    // 삭제
    @GetMapping("/delete")
    public String deleteSupport(@RequestParam(required = false) Long supportId, Model model) {

        Support support = supportRepository.findById(supportId).get();
        supportRepository.delete(support);
        log.info("===DELETE=== (" +support + ")");
        return "redirect:/search?";
    }

}