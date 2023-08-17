package com.example.smstest.domain.support.controller;


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
        Map<String, Integer> supportTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("지원형태");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            supportTypeRatios.put(supportType, supportCount);
        }
        List<Integer> supportTypeRatiosValues = new ArrayList<>(supportTypeRatios.values());

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

        List<Integer> customerTypeRatiosValues = new ArrayList<>(customerTypeRatios.values());

        model.addAttribute("customerTypeRatiosValues", customerTypeRatiosValues);
        model.addAttribute("customerTypeRatios", customerTypeRatios);


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

        List<Integer> teamTypeRatiosValues = new ArrayList<>(teamTypeRatios.values());

        model.addAttribute("teamTypeRatiosValues", teamTypeRatiosValues);
        model.addAttribute("teamTypeRatios", teamTypeRatios);

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

        model.addAttribute("productTypeRatios", productTypeRatios);
        model.addAttribute("productTypeRatiosValues", productTypeRatiosValues);

        /**
         * 제품별
         */
        sql = "SELECT 이슈구분, 지원내역수 \n" +
                "FROM 지원결과_이슈별;";
        supportTypeCounts = jdbcTemplate.queryForList(sql);

        Map<String, Integer> issueTypeRatios = new HashMap<>();
        for (Map<String, Object> row : supportTypeCounts) {
            String supportType = (String) row.get("이슈구분");
            int supportCount = ((Number) row.get("지원내역수")).intValue();
            issueTypeRatios.put(supportType, supportCount);
        }
        List<Integer> issueTypeRatiosValues = new ArrayList<>(issueTypeRatios.values());

        model.addAttribute("issueTypeRatios", issueTypeRatios);
        model.addAttribute("issueTypeRatiosValues", issueTypeRatiosValues);

        /**
         * 인원현황_직급별
         */

        List<Object[]> jobPositionCounts = mempRepository.countEmployeesByJobPosition();
        Map<String, Integer> employPositionRatios = new HashMap<>();

        for (Object[] row : jobPositionCounts) {
            String jobPosition = (String) row[0];
            Long count = (Long) row[1];
            employPositionRatios.put(jobPosition, count.intValue());
        }

        Comparator<String> jobPositionComparator = (position1, position2) -> {
            List<String> order = Arrays.asList("수석", "책임", "선임", "주임", "엔지니어", "인턴");
            int index1 = order.indexOf(position1);
            int index2 = order.indexOf(position2);
            return Integer.compare(index1, index2);
        };

        Map<String, Integer> sortedEmployPositionRatios = new TreeMap<>(jobPositionComparator);
        sortedEmployPositionRatios.putAll(employPositionRatios);

        List<Integer> employPositionRatiosValues = new ArrayList<>(sortedEmployPositionRatios.values());

        model.addAttribute("employPositionRatios", sortedEmployPositionRatios);
        model.addAttribute("employPositionRatiosValues", employPositionRatiosValues);

        return "main";
    }

}