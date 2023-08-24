package com.example.smstest.domain.main.controller;


import com.example.smstest.domain.team.repository.MempRepository;
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

    @GetMapping("/")
    public String main(Model model) {

        return "main";
    }

}