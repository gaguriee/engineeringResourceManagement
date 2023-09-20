package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.client.entity.Customer;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.client.repository.CustomerRepository;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.*;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SupportCRUDController {
    private final SupportService supportService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final IssueRepository issueRepository;
    private final IssueCategoryRepository issueCategoryRepository;
    private final CustomerRepository customerRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final SupportRepository supportRepository;
    private final ProjectRepository projectRepository;


    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 필터링
    @GetMapping("/search")
    public String searchSupportByFilters(@RequestParam(required = false) String customerName,
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
        criteria.setCustomerName(customerName);
        criteria.setTeamId(teamId);
        criteria.setProductId(productId);
        criteria.setIssueId(issueId);
        criteria.setStateId(stateId);
        criteria.setEngineerId(engineerId);
        criteria.setTaskKeyword(Keyword);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<SupportResponse> responsePage = supportService.searchSupportByFilters(criteria, pageable, sortOrder);

        model.addAttribute("posts", responsePage);
        model.addAttribute("totalPages", responsePage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지

        // Product 엔티티
        List<Product> allProducts = productRepository.findAll();
        List<ProductCategory> allProductCategories = productCategoryRepository.findAll();
        model.addAttribute("allProductCategories", allProductCategories);

        // Issue 엔티티
        List<Issue> allIssues = issueRepository.findAll();
        List<IssueCategory> allIssueCategories = issueCategoryRepository.findAllOrderedByPriority();
        for (IssueCategory category : allIssueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        model.addAttribute("allIssueCategories", allIssueCategories);

        // State 엔티티
        List<State> allStates = stateRepository.findAll();

        // Team 엔티티
        List<Team> allTeams = teamRepository.findAll();

        // Member 엔티티
        List<Memp> allMemps = mempRepository.findAll();

        // Customer 엔티티
        List<Customer> allCustomers = customerRepository.findByOrderBySupportCountDesc();

        Collections.sort(allProducts, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allIssues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allStates, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allTeams, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allMemps, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("allProducts", allProducts);
        model.addAttribute("allIssues", allIssues);
        model.addAttribute("allStates", allStates);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("allMemps", allMemps);
        model.addAttribute("allCustomers", allCustomers);


        model.addAttribute("sortOrder", sortOrder);

        model.addAttribute("selectedProducts", productId != null ? productRepository.findAllById(productId) : new ArrayList<>());
        model.addAttribute("selectedIssues", issueId != null ? issueRepository.findAllById(issueId) : new ArrayList<>());
        model.addAttribute("selectedStates", stateId != null ? stateRepository.findAllById(stateId) : new ArrayList<>());
        model.addAttribute("selectedTeams", teamId != null ? teamRepository.findAllById(teamId) : new ArrayList<>());
        model.addAttribute("selectedMemps", engineerId != null ? mempRepository.findAllById(engineerId) : new ArrayList<>());
        model.addAttribute("selectedcustomerName", customerName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            model.addAttribute("startDate", dateFormat.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("endDate", dateFormat.format(endDate));
        }

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);

        return "board";
    }


    // 상세보기
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Long supportId, Model model) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        SupportResponse supportResponse = supportService.getDetails(supportId);
        model.addAttribute("support", supportResponse);
        model.addAttribute("user", user);

        return "details";
    }

    // 등록하기
    @PostMapping("/post")
    public String createSupport(@ModelAttribute SupportRequest supportRequest, RedirectAttributes redirectAttributes) {

        SupportResponse supportResponse = supportService.createSupport(supportRequest);

        return "redirect:/details?supportId="+supportResponse.getId();
    }

    @GetMapping("/create")
    public String createView(Model model) {

        List<Customer> customers = customerRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<IssueCategory> issueCategories = issueCategoryRepository.findAllOrderedByPriority();
        for (IssueCategory category : issueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        List<Project> projects = projectRepository.findAll();

        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("projects", projects);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
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
        List<IssueCategory> issueCategories = issueCategoryRepository.findAllOrderedByPriority();
        for (IssueCategory category : issueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        List<Project> projects = projectRepository.findAll();

        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("projects", projects);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
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
    @PostMapping("/delete")
    public String deleteSupport(@RequestParam(required = false) Long supportId, Model model) {

        supportService.deleteSupport(supportId);

        return "redirect:/search?";
    }

}