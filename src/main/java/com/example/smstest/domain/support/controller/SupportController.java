package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Issue;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.IssueRepository;
import com.example.smstest.domain.support.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SupportController {
    private final SupportService supportService;
    private final ProductRepository productRepository;
    private final IssueRepository issueRepository;

    public SupportController(SupportService supportService, ProductRepository productRepository, IssueRepository issueRepository) {
        this.supportService = supportService;
        this.productRepository = productRepository;
        this.issueRepository = issueRepository;
    }

    // 작업제목 + 작업요약 통합 검색
    @GetMapping("/search")
    public List<SupportResponse> searchBoards(@RequestParam(name = "keyword") String keyword) {
        return supportService.searchSupports(keyword);
    }

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
        model.addAttribute("posts", result);
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

}