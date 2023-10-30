package com.example.smstest.domain.support.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.wbs.entity.Project;
import com.example.smstest.domain.wbs.repository.ProjectRepository;
import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.file.*;
import com.example.smstest.domain.support.repository.*;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class SupportCRUDController {
    private final SupportService supportService;
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
    private final FileService fileService;
    private final FileRepository fileRepository;


    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 필터링
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

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
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


    // 상세보기
    @GetMapping("/details")
    public String getDetails(@RequestParam(required = false) Long supportId, Model model) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SupportResponse supportResponse = supportService.getDetails(supportId);

        model.addAttribute("support", supportResponse);
        model.addAttribute("user", user);

        return "supportDetail";
    }

    // 등록하기
    @PostMapping("/post")
    public String createSupport(@RequestParam("files") List<MultipartFile> files, @ModelAttribute SupportRequest supportRequest, RedirectAttributes redirectAttributes) {

        try {
            List<File> savedFiles = new ArrayList<>();
            for(MultipartFile file : files) {
                if (file.getOriginalFilename().isEmpty())
                    continue;
                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString();
                /* 실행되는 위치의 'files' 폴더에 파일이 저장됩니다. */
                String savePath;
                String filePath;

                // OS 따라 구분자 분리
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")){
                    savePath = System.getProperty("user.dir") + "\\files";
                    filePath = savePath + "\\" + filename;
                }
                else{
                    savePath = System.getProperty("user.dir") + "/files";
                    filePath = savePath + "/" + filename;
                }


                /* 파일이 저장되는 폴더가 없으면 폴더를 생성합니다. */
                if (!new java.io.File(savePath).exists()) {
                    try{
                        new java.io.File(savePath).mkdir();
                    }
                    catch(Exception e){
                        e.getStackTrace();
                    }
                }

                file.transferTo(new java.io.File(filePath));

                FileDto fileDto = new FileDto();
                fileDto.setOrigFilename(origFilename);
                fileDto.setSize(file.getSize());
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

//                새 첨부 파일 저장
                savedFiles.add(fileService.saveFile(fileDto));
            }


            SupportResponse supportResponse = supportService.createSupport(supportRequest);

            for (File savedFile : savedFiles){
                savedFile.setSupportId(supportResponse.getId());
                fileRepository.save(savedFile);
            }

            return "redirect:/details?supportId="+supportResponse.getId();

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }


    }

    @GetMapping("/create")
    public String createView(Model model) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
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
        List<Project> projects = projectRepository.findAll();

        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("projects", projects);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "supportCreate";
    }


    // 수정

    @GetMapping("/modify")
    public String modifyView(@RequestParam(required = false) Long supportId, Model model) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
        List<Project> projects = projectRepository.findAll();

        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(issues, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(products, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("user", user);

        model.addAttribute("customers", customers);
        model.addAttribute("projects", projects);
        model.addAttribute("issues", issues);
        model.addAttribute("issueCategories", issueCategories);
        model.addAttribute("states", states);
        model.addAttribute("products", products);
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("memps", memps);
        model.addAttribute("supportTypes", supportTypes);

        return "supportModify";
    }
    @PostMapping("/modify")
    public String modifySupport(@RequestParam("files") List<MultipartFile> files, @ModelAttribute ModifyRequest modifyRequest, RedirectAttributes redirectAttributes) {

        try {

            if (modifyRequest.getDeletedFileId() != null){
                fileRepository.deleteAllById(modifyRequest.getDeletedFileId());
            }
            List<File> savedFiles = new ArrayList<>();

            for(MultipartFile file : files) {
                if (file.getOriginalFilename().isEmpty())
                    continue;
                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString();
                /* 실행되는 위치의 'files' 폴더에 파일이 저장됩니다. */
                String savePath;
                String filePath;

                // OS 따라 구분자 분리
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")){
                    savePath = System.getProperty("user.dir") + "\\files";
                    filePath = savePath + "\\" + filename;
                }
                else{
                    savePath = System.getProperty("user.dir") + "/files";
                    filePath = savePath + "/" + filename;
                }


                /* 파일이 저장되는 폴더가 없으면 폴더를 생성합니다. */
                if (!new java.io.File(savePath).exists()) {
                    try{
                        new java.io.File(savePath).mkdir();
                    }
                    catch(Exception e){
                        e.getStackTrace();
                    }
                }

                file.transferTo(new java.io.File(filePath));

                FileDto fileDto = new FileDto();
                fileDto.setOrigFilename(origFilename);
                fileDto.setSize(file.getSize());
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

//                새 첨부 파일 저장
                savedFiles.add(fileService.saveFile(fileDto));
            }

            SupportResponse supportResponse = supportService.modifySupport(modifyRequest);

            for (File savedFile : savedFiles){
                savedFile.setSupportId(supportResponse.getId());
                fileRepository.save(savedFile);
            }

            return "redirect:/details?supportId="+supportResponse.getId();

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }
    }

    // 삭제
    @PostMapping("/delete")
    public String deleteSupport(@RequestParam(required = false) Long supportId, Model model) {

        supportService.deleteSupport(supportId);

        return "redirect:/search?";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        // 파일 이름을 UTF-8로 인코딩
        String encodedFileName = new String(fileDto.getOrigFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));

        // Content-Disposition 헤더에 올바른 파일 이름 설정
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}