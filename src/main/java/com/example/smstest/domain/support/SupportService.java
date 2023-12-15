package com.example.smstest.domain.support;

import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.file.*;
import com.example.smstest.domain.project.Project;
import com.example.smstest.domain.project.ProjectRepository;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import com.example.smstest.external.license.LicenseProject;
import com.example.smstest.external.license.LicenseProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportService  {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;
    private final StateRepository stateRepository;
    private final ProductRepository productRepository;
    private final MempRepository mempRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final ProjectRepository projectRepository;
    private final LicenseProjectRepository licenseProjectRepository;
    private final ClientRepository clientRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    public Page<SupportResponse> searchSupportByFilters(
            SupportFilterCriteria criteria, Pageable pageable, String sortOrder) {
        Page<Support> result = supportRepository.searchSupportByFilters(criteria, pageable, sortOrder);
        return new PageImpl<>(
                result.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements());
    }
    public SupportResponse getDetails(Long supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return SupportResponse.entityToResponse(support);
    }

    public SupportResponse createSupport(List<MultipartFile> files,  SupportRequest supportRequest) throws NoSuchAlgorithmException, IOException {

        Support support = new Support();

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean containsSuperAdmin = user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN"));

        if (!containsSuperAdmin && isBeforeSevenDays(supportRequest.getSupportDate())){
            throw new CustomException(ErrorCode.DATE_INVALID);
        }

        LicenseProject licenseProject = licenseProjectRepository.findById(supportRequest.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        Optional<Project> project = projectRepository.findFirstByProjectGuid(supportRequest.getProjectId());

        if (project.isPresent()){
            project.get().updateProject(
                    licenseProject.getProjectName()
            );

            support.setProject(projectRepository.save(project.get()));
        }
        else {

            if (!clientRepository.existsByCompanyGuid(licenseProject.getCompany().getCompanyGuid())) {
                Client newClient = new Client();
                newClient.setName(licenseProject.getCompany().getCompanyName());
                newClient.setCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setCompanyRegDate(licenseProject.getCompany().getCompanyRegDate());

                clientRepository.save(newClient);
                log.info("Saved Client :: " + newClient.getName());
            }
            else{
                Client newClient = clientRepository.findFirstByCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setName(licenseProject.getCompany().getCompanyName());
                clientRepository.save(newClient);
            }

            Client client = clientRepository.findFirstByCompanyGuid(licenseProject.getCompany().getCompanyGuid());

            Project newProject = Project.builder()
                    .client(client)
                    .name(licenseProject.getProjectName())
                    .uniqueCode(licenseProject.getProjectCode())
                    .projectRegDate(licenseProject.getProjectRegDate())
                    .projectGuid(licenseProject.getProjectGuid())
                    .build();
           ;

            support.setProject(projectRepository.save(newProject));

            log.info("Saved Project :: " + newProject.getName() + " (" + newProject.getUniqueCode() + ")");

        }

        support.setSupportDate(supportRequest.getSupportDate());
        support.setRedmineIssue(supportRequest.getRedmineIssue());
        support.setTaskTitle(supportRequest.getTaskTitle());
        support.setTaskSummary(supportRequest.getTaskSummary());
        support.setTaskDetails(supportRequest.getTaskDetails());
        support.setSupportTypeHour(supportRequest.getSupportTypeHour());

        support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findById(supportRequest.getEngineerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
        support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);

        saveFile(files, newsupport.getId());

        log.info("===CREATE=== ("+ SupportResponse.entityToResponse(newsupport) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());

        return SupportResponse.entityToResponse(newsupport);
    }

    private static boolean isBeforeSevenDays(Date supportDate) {
        long sevenDaysAgoMillis = System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000;
        Date sevenDaysAgo = new Date(sevenDaysAgoMillis);
        return supportDate.before(sevenDaysAgo);
    }

    public SupportResponse modifySupport(ModifyRequest supportRequest) {

        Support support = supportRepository.findById(supportRequest.getSupportId()).get();

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean containsSuperAdmin = user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN"));

        if (!containsSuperAdmin && isBeforeSevenDays(supportRequest.getSupportDate())){
            throw new CustomException(ErrorCode.DATE_INVALID);
        }

        LicenseProject licenseProject = licenseProjectRepository.findById(supportRequest.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        Optional<Project> project = projectRepository.findFirstByProjectGuid(supportRequest.getProjectId());

        if (project.isPresent()){
            project.get().updateProject(
                    licenseProject.getProjectName()
            );
            projectRepository.save(project.get());

            support.setProject(project.get());
        }
        else {

            if (!clientRepository.existsByCompanyGuid(licenseProject.getCompany().getCompanyGuid())) {
                Client newClient = new Client();
                newClient.setName(licenseProject.getCompany().getCompanyName());
                newClient.setCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setCompanyRegDate(licenseProject.getCompany().getCompanyRegDate());

                clientRepository.save(newClient);
                log.info("Saved Client :: " + newClient.getName());
            }
            else{
                Client newClient = clientRepository.findFirstByCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setName(licenseProject.getCompany().getCompanyName());
                clientRepository.save(newClient);
            }

            Client client = clientRepository.findFirstByCompanyGuid(licenseProject.getCompany().getCompanyGuid());

            Project newProject = Project.builder()
                    .client(client)
                    .name(licenseProject.getProjectName())
                    .uniqueCode(licenseProject.getProjectCode())
                    .projectRegDate(licenseProject.getProjectRegDate())
                    .projectGuid(licenseProject.getProjectGuid())
                    .build();
            projectRepository.save(newProject);
            log.info("Saved Project :: " + newProject.getName() + " (" + newProject.getUniqueCode() + ")");

        }


        if (support.getEngineer().getUsername().equals(user.getUsername())
        || user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN"))){
            support.setSupportDate(supportRequest.getSupportDate());
            support.setRedmineIssue(supportRequest.getRedmineIssue());
            support.setTaskTitle(supportRequest.getTaskTitle());
            support.setTaskSummary(supportRequest.getTaskSummary());
            support.setTaskDetails(supportRequest.getTaskDetails());
            support.setSupportTypeHour(supportRequest.getSupportTypeHour());

            support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
            support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
            support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
            support.setEngineer(mempRepository.findById(supportRequest.getEngineerId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
            support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

            // Support 엔티티를 저장하고 반환
            Support savedsupport = supportRepository.save(support);
            log.info("===MODIFY=== ("+ SupportResponse.entityToResponse(savedsupport) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());

            return SupportResponse.entityToResponse(savedsupport);
        }

        return null;
    }

    public void deleteSupport(Long supportId) {
        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Support support = supportRepository.findById(supportId).orElse(null);

        if (support != null && (support.getEngineer().getUsername().equals(user.getUsername())
                || user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN")))){
            supportRepository.delete(support);
            log.info("===DELETE=== ("+ SupportResponse.entityToResponse(support) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    private List<File> saveFile(List<MultipartFile> files, Long supportId) throws NoSuchAlgorithmException, IOException {
        List<File> savedFiles = new ArrayList<>();

        for(MultipartFile file : files) {
            if (file.getOriginalFilename().isEmpty())
                continue;
            String origFilename = file.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();

            String savePath;
            String filePath;

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")){
                savePath = System.getProperty("user.dir") + "\\files\\support";
                filePath = savePath + "\\" + filename;
            }
            else{
                savePath = System.getProperty("user.dir") + "/files/support";
                filePath = savePath + "/" + filename;
            }

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

            savedFiles.add(fileService.saveFile(fileDto));
        }

        // 저장된 File 엔티티들을 대상으로, 첨부대상인 support의 id를 외래키로 매핑해줌
        for (File savedFile : savedFiles){
            savedFile.setSupportId(supportId);
            fileRepository.save(savedFile);
        }


        return savedFiles;
    }

}
