package com.example.smstest.domain.main.controller;


import com.example.smstest.domain.support.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class MainController {
    private final MempRepository mempRepository;
    private final JdbcTemplate jdbcTemplate;

    public MainController(MempRepository mempRepository, JdbcTemplate jdbcTemplate) {
        this.mempRepository = mempRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 메인페이지
    @GetMapping("/")
    public String main(Model model) {

        return "main";
    }

}