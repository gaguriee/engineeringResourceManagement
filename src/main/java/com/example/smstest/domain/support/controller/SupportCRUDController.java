package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.file.FileDto;
import com.example.smstest.domain.file.FileService;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.domain.support.service.SupportService;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 지원내역 관련 Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class SupportCRUDController {
    private final SupportService supportService;
    private final FileService fileService;

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final IssueRepository issueRepository;
    private final IssueCategoryRepository issueCategoryRepository;
    private final ClientRepository clientRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final SupportRepository supportRepository;
    private final ProjectRepository projectRepository;


    /**
     * 날짜 형태를 자동으로 bind해주는 메소드
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 지원내역 조회 (QueryDSL 사용)
     * @param customerName 고객사명 (String)
     * @param projectName 프로젝트명 (String)
     * @param teamId 팀 id
     * @param productId 제품 id
     * @param issueId 이슈 id
     * @param stateId 상태(업무) id
     * @param engineerId 엔지니어 id
     * @param Keyword 검색 키워드
     * @param startDate 시작일
     * @param endDate 종료일
     * @param sortOrder 정렬방식 (desc, asc)
     * @param pageable 페이지네이션할 때 사용하는 파라미터 (현재 페이지, 총 페이지 수, offset 등)
     * @param model Controller 에서 생성된 데이터를 담아 View 로 전달할 때 사용하는 객체 ( key:value 형식 )
     * @return 지원내역 검색 결과 뷰
     */
    @GetMapping("/search")
    public String searchSupportByFilters(@RequestParam(required = false) String customerName,
                                         @RequestParam(required = false) String projectName,
                                         @RequestParam(required = false) List<Integer> teamId,
                                         @RequestParam(required = false) List<Long> productId,
                                         @RequestParam(required = false) List<Long> issueId,
                                         @RequestParam(required = false) List<Long> stateId,
                                         @RequestParam(required = false) List<Long> engineerId,
                                         @RequestParam(required = false) String Keyword,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                                         @RequestParam(required = false, defaultValue = "desc")  String sortOrder, // 추가된 파라미터
                                         Pageable pageable,
                                         Model model) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SupportFilterCriteria criteria = new SupportFilterCriteria();
        criteria.setCustomerName(customerName);
        criteria.setProjectName(projectName);
        criteria.setTeamId(teamId);
        criteria.setProductId(productId);
        criteria.setIssueId(issueId);
        criteria.setStateId(stateId);
        criteria.setEngineerId(engineerId);
        criteria.setTaskKeyword(Keyword);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<SupportResponse> responsePage = supportService.searchSupportByFilters(criteria, pageable, sortOrder);


        model.addAttribute("posts", responsePage);
        model.addAttribute("totalPages", responsePage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("currentPage", pageable.getPageNumber()); // 현재 페이지

        // Product 엔티티
        List<Product> allProducts = productRepository.findAll();
        List<ProductCategory> allProductCategories = productCategoryRepository.findAll();
        model.addAttribute("allProductCategories", allProductCategories);

        // Issue 엔티티
        List<Issue> allIssues = issueRepository.findAll();
        List<IssueCategory> allIssueCategories = issueCategoryRepository.findAllOrderedByPriority(user.getTeam().getDepartment().getDivision().getId());
        for (IssueCategory category : allIssueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        model.addAttribute("allIssueCategories", allIssueCategories);

        // State 엔티티
        List<State> allStates = stateRepository.findAll();

        // Team 엔티티
        List<Team> allTeams = teamRepository.findAll();

        // Member 엔티티
        List<Memp> allMemps = mempRepository.findAll();

        // Client 엔티티
        List<Client> allCustomers = clientRepository.findByOrderBySupportCountDesc();

        // Project 엔티티
        List<Project> allProjects = projectRepository.findAllByOrderBySupportCountDesc();

        Collections.sort(allProducts, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allIssues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allStates, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allTeams, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(allMemps, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("allProducts", allProducts);
        model.addAttribute("allIssues", allIssues);
        model.addAttribute("allStates", allStates);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("allMemps", allMemps);
        model.addAttribute("allCustomers", allCustomers);
        model.addAttribute("allProjects", allProjects);
        model.addAttribute("sortOrder", sortOrder);

        model.addAttribute("selectedProducts", productId != null ? productRepository.findAllById(productId) : new ArrayList<>());
        model.addAttribute("selectedIssues", issueId != null ? issueRepository.findAllById(issueId) : new ArrayList<>());
        model.addAttribute("selectedStates", stateId != null ? stateRepository.findAllById(stateId) : new ArrayList<>());
        model.addAttribute("selectedTeams", teamId != null ? teamRepository.findAllById(teamId) : new ArrayList<>());
        model.addAttribute("selectedMemps", engineerId != null ? mempRepository.findAllById(engineerId) : new ArrayList<>());
        model.addAttribute("selectedcustomerName", customerName);
        model.addAttribute("selectedprojectName", projectName);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            model.addAttribute("startDate", dateFormat.format(startDate));
        }
        if (endDate != null) {
            model.addAttribute("endDate", dateFormat.format(endDate));
        }

        model.addAttribute("user", user);

        return "supportBoard";
    }


    /**
     * 지원내역 상세 보기
     * @param supportId 지원내역 id
     * @param model
     * @return
     */
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Long supportId, Model model) {
        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SupportResponse supportResponse = supportService.getDetails(supportId);

        model.addAttribute("support", supportResponse);
        model.addAttribute("user", user);

        return "supportDetail";
    }

    /**
     * 지원내역 등록 뷰 리턴
     * @param model
     * @return
     */
    @GetMapping("/create")
    public String createView(Model model) {
        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Client> customers = clientRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<IssueCategory> issueCategories = issueCategoryRepository.findAllOrderedByPriority(user.getTeam().getDepartment().getDivision().getId());
        for (IssueCategory category : issueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();
        List<ProductCategory> productCategories = productCategoryRepository.findAll();

        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "supportCreate";
    }

    /**
     * 지원내역 등록하기
     * @param files 업로드 시 받아오는 파일 리스트
     * @param supportRequest 지원내역 등록/수정 시 매핑되는 DTO
     * @return
     */
    @PostMapping("/post")
    public String createSupport(@RequestParam("files") List<MultipartFile> files, @ModelAttribute SupportRequest supportRequest) {

        try {

            SupportResponse supportResponse = supportService.createSupport(files, supportRequest);

            return "redirect:/details?supportId="+supportResponse.getId();

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }


    }


    /**
     * 지원내역 수정 뷰 리턴
     * - 등록과 거의 동일, 해당 support Id로 기존 정보 가져와서 수정페이지에 보여줌
     * @param supportId
     * @param model
     * @return
     */
    @GetMapping("/modify")
    public String modifyView(@RequestParam(required = false) Long supportId, Model model) {
        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 기존 등록 정보 찾기
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        model.addAttribute("support", support);

        List<Client> customers = clientRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<IssueCategory> issueCategories = issueCategoryRepository.findAllOrderedByPriority(user.getTeam().getDepartment().getDivision().getId());
        for (IssueCategory category : issueCategories) {
            Collections.sort(category.getIssues(), Comparator.comparingInt(Issue::getPriority));
        }
        List<State> states = stateRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Memp> memps = mempRepository.findAll();
        List<SupportType> supportTypes = supportTypeRepository.findAll();
        List<ProductCategory> productCategories = productCategoryRepository.findAll();

        // 리스트 소팅 메소드 (각각의 이름 기존 오름차순 정렬)
        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "supportModify";
    }

    /**
     * 지원내역 수정하기
     * - 등록과 거의 동일, 등록은 신규 엔티티를 생성 후 save하는 것이라면, 수정은 기존 엔티티를 supportId로 검색 후 update한 뒤 save
     * @param files
     * @param modifyRequest
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/modify")
    public String modifySupport(@RequestParam("files") List<MultipartFile> files, @ModelAttribute ModifyRequest modifyRequest, RedirectAttributes redirectAttributes) {

        try {

            SupportResponse supportResponse = supportService.modifySupport(files, modifyRequest);

            return "redirect:/details?supportId="+supportResponse.getId();

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }
    }

    /**
     * 지원내역 삭제
     * @param supportId
     * @return 지원내역 조회 페이지로 리다이렉트
     */
    @PostMapping("/delete")
    public String deleteSupport(@RequestParam(required = false) Long supportId) {

        supportService.deleteSupport(supportId);

        return "redirect:/search?";
    }

    /**
     * 파일 다운로드 메소드
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        try{
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            String encodedFileName = new String(fileDto.getOrigFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/octet-stream"));

            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }
        catch (Exception e){
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }

    }

}