package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.dto.SupportSummary;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/customer")
public class CustomerCRUDController {
    private final CustomerRepository customerRepository;

    public CustomerCRUDController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 검색
    @GetMapping("/search")
    public String searchSupportByFilters(
                                         @RequestParam(required = false) String Keyword,
                                         Pageable pageable,
                                         Model model) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Customer> result = customerRepository.findAllBySupportCount(pageable);

        if (Keyword != null){
            result = customerRepository.findByNameContainingOrderBySupportCountDesc(Keyword, pageable);
        }

        model.addAttribute("customers", result);
        model.addAttribute("totalPages", result.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지
        return "customer";
    }


    // 상세보기
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Long customerId, Model model) {

        Customer customer = customerRepository.findById(customerId).get();
        model.addAttribute("customer", customer);
        List<SupportSummary> supportSummary = customerRepository.getSupportSummaryByCustomerId(customerId);
        model.addAttribute("supportSummary", supportSummary);
        System.out.println(supportSummary);
        return "customerDetails";
    }

//    // 등록하기
//    @PostMapping("/post")
//    public String createSupport(@ModelAttribute SupportRequest supportRequest, RedirectAttributes redirectAttributes) {
//
//        SupportResponse supportResponse = supportService.createSupport(supportRequest);
////        redirectAttributes.addFlashAttribute("support", supportResponse);
//
//        return "redirect:/details?supportId="+supportResponse.getId();
//    }
//
//    @GetMapping("/create")
//    public String createView(Model model) {
//
//        List<Customer> customers = customerRepository.findAll();
//        List<Issue> issues = issueRepository.findAll();
//        List<State> states = stateRepository.findAll();
//        List<Product> products = productRepository.findAll();
//        List<Memp> memps = mempRepository.findAll();
//        List<SupportType> supportTypes = supportTypeRepository.findAll();
//
//        model.addAttribute("customers", customers);
//        model.addAttribute("issues", issues);
//        model.addAttribute("states", states);
//        model.addAttribute("products", products);
//        model.addAttribute("memps", memps);
//        model.addAttribute("supportTypes", supportTypes);
//
//        return "create";
//    }
//

}