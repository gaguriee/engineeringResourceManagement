package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.support.dto.AggregatedDataDTO;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TeamInfoController {
    private final ProductRepository productRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;

    private final TeamRepository teamRepository;
    private final SupportRepository supportRepository;
    private final CustomerRepository customerRepository;

    public TeamInfoController(ProductRepository productRepository, StateRepository stateRepository, MempRepository mempRepository, TeamRepository teamRepository, SupportRepository supportRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.stateRepository = stateRepository;
        this.mempRepository = mempRepository;
        this.teamRepository = teamRepository;
        this.supportRepository = supportRepository;
        this.customerRepository = customerRepository;
    }


    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/team")
    public String selectTeam(Model model) {

        List<Team> teams = teamRepository.findAll();

        model.addAttribute("teams", teams);

        return "team";
    }

    @GetMapping("/teamId")
    public String getTeamInfo(@RequestParam(required = true) Integer teamId, Model model) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            List<Memp> memps = mempRepository.findAllByTeamId(teamId);
            model.addAttribute("team", teamOptional.get());
            model.addAttribute("memps", memps);

            // Collect engineerId and corresponding support count
            Map<Long, Long> engineerSupportCounts = new HashMap<>();
            for (Memp memp : memps) {
                Long engineerId = memp.getId();
                Long supportCount = supportRepository.countByEngineerId(engineerId);
                engineerSupportCounts.put(engineerId, supportCount);
            }

            model.addAttribute("engineerSupportCounts", engineerSupportCounts);


            List<State> states = stateRepository.findAll();
            model.addAttribute("states", states);

            Map<Long, Long> stateSupportCounts = new HashMap<>();
            for (State state : states) {
                Long supportCount = supportRepository.countByTeamIdAndStateId(teamId, state.getId());
                stateSupportCounts.put(state.getId(), supportCount);
            }

            model.addAttribute("stateSupportCounts", stateSupportCounts);

            List<Product> products = productRepository.findAll();
            model.addAttribute("products", products);

            Map<Long, Long> productSupportCounts = new HashMap<>();
            for (Product product : products) {
                Long supportCount = supportRepository.countByTeamIdAndProductId(teamId, product.getId());
                productSupportCounts.put(product.getId(), supportCount);
            }

            model.addAttribute("productSupportCounts", productSupportCounts);


            List<Support> supports = supportRepository.findByTeamId(teamId);
            model.addAttribute("supports", supports);

        } else {
            // 팀을 찾지 못한 경우, 적절한 처리를 수행하거나 에러 메시지를 뷰로 전달할 수 있습니다.
            model.addAttribute("errorMessage", "해당 팀을 찾을 수 없습니다.");
        }

        return "teamInfo";
    }

    // 팀원 정보 조회
    @GetMapping("/memberInfo")
    public String getmemberInfo(@RequestParam(required = true) Long memberId, Model model) {
        Optional<Memp> memp = mempRepository.findById(memberId);
        List<Support> supports = supportRepository.findByEngineerId(memberId);
        Optional<Team> team = teamRepository.findById(memp.get().getTeamId());

        model.addAttribute("member", memp.get());
        model.addAttribute("team", team.get());
        model.addAttribute("supports", supports);

        List<Object[]> aggregatedData = supportRepository.countAttributesByEngineer(memberId);

        List<AggregatedDataDTO> dtoList = new ArrayList<>();
        for (Object[] data : aggregatedData) {
            AggregatedDataDTO dto = new AggregatedDataDTO();
            dto.setCustomerId((Long) data[0]);
            dto.setProductId((Long) data[1]);
            dto.setStateId((Long) data[2]);
            dto.setCustomerName(customerRepository.findById(dto.getCustomerId()).get().getName());
            dto.setProductName(productRepository.findById(dto.getProductId()).get().getName());
            dto.setStateName(stateRepository.findById(dto.getStateId()).get().getName());
            dto.setCount((Long) data[3]);
            dtoList.add(dto);
        }

        model.addAttribute("aggregatedData", dtoList);
        return "memberInfo";
    }

    // 팀원 정보 조회
    @GetMapping("/memberInfoDetail")
    public String getMemberInfoDetail(
            @RequestParam(required = false, defaultValue = "desc")  String sortOrder,
            @RequestParam(required = true) Long memberId, Long customerId, Long productId, Long stateId,
            Pageable pageable,
            Model model) {
        Page<Support> supports = supportRepository.findAllByEngineerIdAndCustomerIdAndProductIdAndStateId(memberId, customerId, productId, stateId, pageable);

        Page<SupportResponse> responsePage = new PageImpl<>(
                supports.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                supports.getPageable(),
                supports.getTotalElements());

        model.addAttribute("posts", responsePage);

        model.addAttribute("totalPages", supports.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지

        System.out.println(supports.stream().findFirst());

        return "memberInfoDetail";
    }

}