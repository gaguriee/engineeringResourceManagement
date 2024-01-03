package com.example.smstest.domain.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 고객사 상호작용 관련 Controller
 */
@Controller
@Slf4j
@RequestMapping("/customer")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * [ 고객사 검색 ]
     * @param Keyword 검색 키워드
     * @param pageable
     * @param model 고객사 리스트
     * @return 고객사 리스트 페이지
     */
    @GetMapping("/search")
    public String searchClientByFilters(
            @RequestParam(required = false) String Keyword,
            @PageableDefault(size = 30) Pageable pageable,
            Model model) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Client> result = clientService.searchClients(Keyword, pageable);

        model.addAttribute("customers", result);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        return "clientBoard";
    }

    /**
     * [ 고객사 디테일 ]
     * @param customerId 해당 고객사 id로 검색
     * @param model 검색 결과 SupportSummary 리스트에 저장 후 전달
     * @return 고객사 디테일 페이지
     */
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Integer customerId, Model model) {
        Client client = clientService.getClientDetails(customerId);
        model.addAttribute("customer", client);

        return "clientDetails";
    }
}
