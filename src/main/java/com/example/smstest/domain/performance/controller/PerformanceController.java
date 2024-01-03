package com.example.smstest.domain.performance.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 실적 조회 페이지 매핑 Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/performance")
public class PerformanceController {

    @GetMapping("/inquiry")
    public String performanceInquiry() {

        return "performanceInquiry";
    }

}