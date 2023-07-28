package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportController {
    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    // 작업제목 + 작업요약 통합 검색
    @GetMapping("/search")
    public List<SupportResponse> searchBoards(@RequestParam(name = "keyword") String keyword) {
        return supportService.searchSupports(keyword);
    }

    // 필터링
    @GetMapping("/filter")
    public ResponseEntity<List<SupportResponse>> getFilteredPosts(
            @RequestParam(name = "issueId", required = false) Long issueId,
            @RequestParam(name = "stateId", required = false) Long stateId,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "customerId", required = false) Long customerId) {

        List<SupportResponse> filteredPosts = supportService.getFilteredPosts(issueId, stateId, productId, customerId);
        return ResponseEntity.ok(filteredPosts);
    }

    @PostMapping("")
    public ResponseEntity<SupportResponse> createSupport(@RequestBody SupportRequest supportRequest) {
        SupportResponse newSupport = supportService.createSupport(supportRequest);
        return ResponseEntity.ok(newSupport);
    }

}