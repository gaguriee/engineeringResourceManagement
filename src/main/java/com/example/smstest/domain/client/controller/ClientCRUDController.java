package com.example.smstest.domain.client.controller;


import com.example.smstest.domain.client.entity.Customer;
import com.example.smstest.domain.client.service.CustomerServiceImpl;
import com.example.smstest.domain.support.dto.SupportSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/customer")
public class ClientCRUDController {
    private final CustomerServiceImpl customerServiceImpl;

    @Autowired
    public ClientCRUDController(CustomerServiceImpl customerServiceImpl) {
        this.customerServiceImpl = customerServiceImpl;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/search")
    public String searchSupportByFilters(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Customer> result = customerServiceImpl.searchCustomers(Keyword, pageable);

        model.addAttribute("customers", result);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        return "customer";
    }

    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Integer customerId, Model model) {
        Customer customer = customerServiceImpl.getCustomerDetails(customerId);
        model.addAttribute("customer", customer);

        List<SupportSummary> supportSummary = customerServiceImpl.getSupportSummaryByCustomerId(customerId);
        model.addAttribute("supportSummary", supportSummary);

        return "customerDetails";
    }
}
