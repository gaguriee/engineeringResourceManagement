package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SupportController {
    private final SupportService supportService;
    private final ProductRepository productRepository;
    private final IssueRepository issueRepository;
    private final CustomerRepository customerRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;

    private final SupportTypeRepository supportTypeRepository;

    private final JdbcTemplate jdbcTemplate;

    public SupportController(SupportService supportService, ProductRepository productRepository, IssueRepository issueRepository, CustomerRepository customerRepository, StateRepository stateRepository, MempRepository mempRepository, SupportTypeRepository supportTypeRepository, JdbcTemplate jdbcTemplate) {
        this.supportService = supportService;
        this.productRepository = productRepository;
        this.issueRepository = issueRepository;
        this.customerRepository = customerRepository;
        this.stateRepository = stateRepository;
        this.mempRepository = mempRepository;
        this.supportTypeRepository = supportTypeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 메인페이지
    @GetMapping("/main")
    public String main(Model model) {

        /**
         * 지원형태별
         */
        String sql = "SELECT 지원형태, 지원내역수 FROM 지원결과_지원형태별";
        List<Map<String, Object>> supportTypeCounts = jdbcTemplate.queryForList(sql);
        int totalSupportCount = 0;
        for (Map<String, Object> row : supportTypeCounts) {
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            totalSupportCount += supportCount;
        }
        Map<String, Double> supportTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("지원형태");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            double ratio = (double) supportCount / totalSupportCount * 100.0;
            supportTypeRatios.put(supportType, ratio);
        }
        List<Double> supportTypeRatiosValues = new ArrayList<>(supportTypeRatios.values());

        model.addAttribute("supportTypeRatiosValues", supportTypeRatiosValues);
        model.addAttribute("supportTypeRatios", supportTypeRatios);

        /**
         * 고객사별
         */
        sql = "SELECT 고객사, 지원내역수 \n" +
                "FROM 지원결과_고객사별 \n" +
                "ORDER BY 지원내역수 DESC \n" +
                "LIMIT 10;";
        supportTypeCounts = jdbcTemplate.queryForList(sql);

        Map<String, Integer> customerTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("고객사");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            customerTypeRatios.put(supportType, supportCount);
        }

        // Sort the customerTypeRatios by values in descending order
        LinkedHashMap<String, Integer> sortedCustomerTypeRatios = customerTypeRatios.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        List<Integer> customerTypeRatiosValues = new ArrayList<>(sortedCustomerTypeRatios.values());

        model.addAttribute("customerTypeRatiosValues", customerTypeRatiosValues);
        model.addAttribute("customerTypeRatios", sortedCustomerTypeRatios);


        /**
         * 팀별
         */
        sql = "SELECT 소속, 지원내역수 \n" +
                "FROM 지원결과_팀별;";
        supportTypeCounts = jdbcTemplate.queryForList(sql);

        Map<String, Integer> teamTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("소속");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            teamTypeRatios.put(supportType, supportCount);
        }

        // Sort the customerTypeRatios by values in descending order
        LinkedHashMap<String, Integer> sortedTeamTypeRatios = teamTypeRatios.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        List<Integer> teamTypeRatiosValues = new ArrayList<>(sortedTeamTypeRatios.values());

        model.addAttribute("teamTypeRatiosValues", teamTypeRatiosValues);
        model.addAttribute("teamTypeRatios", sortedTeamTypeRatios);

        /**
         * 제품별
         */
        sql = "SELECT 제품, 지원내역수 \n" +
                "FROM 지원결과_제품별;";
        supportTypeCounts = jdbcTemplate.queryForList(sql);

        Map<String, Integer> productTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("제품");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            productTypeRatios.put(supportType, supportCount);
        }
        List<Integer> productTypeRatiosValues = new ArrayList<>(productTypeRatios.values());
        LinkedHashMap<String, Integer> sortedProductTypeRatios = productTypeRatios.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        model.addAttribute("productTypeRatios", sortedProductTypeRatios);
        model.addAttribute("productTypeRatiosValues", productTypeRatiosValues);

        return "main";
    }

//    // 작업제목 + 작업요약 통합 검색
//    @GetMapping("/search")
//    public List<SupportResponse> searchBoards(@RequestParam(name = "keyword") String keyword) {
//        return supportService.searchSupports(keyword);
//    }

    // 필터링
    @GetMapping("/filter")
    public String searchSupportByFilters(@RequestParam(required = false) List<Long> customerId,
                                         @RequestParam(required = false) List<Long> teamId,
                                         @RequestParam(required = false) List<Long> productId,
                                         @RequestParam(required = false) List<Long> issueId,
                                         @RequestParam(required = false) List<Long> stateId,
                                         @RequestParam(required = false) List<Long> engineerId,
                                         @RequestParam(required = false) String Keyword,
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
        Page<Support> result = supportService.searchSupportByFilters(criteria, pageable);
        Page<SupportResponse> responsePage = new PageImpl<>(
                result.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements());
        model.addAttribute("posts", responsePage);
        model.addAttribute("totalPages", result.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지

        // Product 엔티티의 모든 데이터를 가져와서 Model에 추가
        List<Product> allProducts = productRepository.findAll();
        model.addAttribute("allProducts", allProducts);

        // Issue 엔티티의 모든 데이터를 가져와서 Model에 추가
        List<Issue> allIssues = issueRepository.findAll();
        model.addAttribute("allIssues", allIssues);

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
    public String createPost(Model model) {

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

}