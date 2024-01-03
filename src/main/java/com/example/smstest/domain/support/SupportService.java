package com.example.smstest.domain.support;

import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.file.*;
import com.example.smstest.domain.project.Project;
import com.example.smstest.domain.project.ProjectRepository;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.domain.support.repository.support.SupportRepository;
import com.example.smstest.external.license.LicenseProject;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
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
    private final ClientRepository clientRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    /**
     * [ 지원내역 조회 ]
     * 필터 조건을 사용해서 검색 후 Page 객체에 담아 반환
     * @param criteria
     * @param pageable
     * @param sortOrder
     * @return
     */
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

    /**
     * [ 지원내역 디테일 ]
     * support Id로 Support Entity를 검색해서 Response DTO (SupportResponse)로 매핑 후 반환
     * @param supportId
     * @return
     */
    public SupportResponse getDetails(Long supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return SupportResponse.entityToResponse(support);
    }

    /**
     * [ 지원내역 등록 ]
     *
     * @param files
     * @param supportRequest
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public SupportResponse createSupport(List<MultipartFile> files,  SupportRequest supportRequest) throws NoSuchAlgorithmException, IOException {

        Support newsupport = saveSupport(new Support(), files, supportRequest);

        log.info("===CREATE=== ("+ SupportResponse.entityToResponse(newsupport) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());

        return SupportResponse.entityToResponse(newsupport);
    }

    // 7일 전 이내의 지원내역인지 확인
    private static boolean isBeforeSevenDays(Date supportDate) {
        long sevenDaysAgoMillis = System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000;
        Date sevenDaysAgo = new Date(sevenDaysAgoMillis);
        return supportDate.before(sevenDaysAgo);
    }

    /**
     * [ 지원내역 수정]
     * @param files
     * @param supportRequest
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public SupportResponse modifySupport(List<MultipartFile> files,  SupportRequest supportRequest) throws NoSuchAlgorithmException, IOException {

        Optional<Support> currentSupport = supportRepository.findById(supportRequest.getSupportId());

        // 삭제된 FileId가 존재할 경우, 기존에 File table에서 해당 id 전부 삭제함
        if (supportRequest.getDeletedFileId() != null){
            fileRepository.deleteAllById(supportRequest.getDeletedFileId());
        }

        if (currentSupport.isPresent()) {
            Support savedSupport = saveSupport(currentSupport.get(), files, supportRequest);
            log.info("===MODIFY=== (" + SupportResponse.entityToResponse(savedSupport) + ") by " + SecurityContextHolder.getContext().getAuthentication().getName());
            return SupportResponse.entityToResponse(savedSupport);
        }
        else {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

    }

    // 지원내역 업데이트
    private Support saveSupport(Support support, List<MultipartFile> files,  SupportRequest supportRequest) throws IOException, NoSuchAlgorithmException {
        Memp user = mempRepository.findFirstByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean containsSuperAdmin = user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN"));

        // Super Admin 권한이 없고 7일 이내의 지원내역이 아닐 경우 DATE_INVALID 에러 반환
        if (!containsSuperAdmin && isBeforeSevenDays(supportRequest.getSupportDate())){
            throw new CustomException(ErrorCode.DATE_INVALID);
        }

        // json으로 받은 license project 객체로 매핑
        ObjectMapper objectMapper = new ObjectMapper();

        LicenseProject licenseProject = objectMapper.readValue(supportRequest.getProject(), LicenseProject.class);

        // 1. ProjectGuid로 로컬 DB에서 해당 프로젝트 검색
        Optional<Project> project = projectRepository.findFirstByProjectGuid(licenseProject.getProjectGuid());

        // 프로젝트 존재 시 업데이트 후 지원내역에 저장
        if (project.isPresent()){
            project.get().updateProject(
                    licenseProject.getProjectName()
            );

            support.setProject(projectRepository.save(project.get()));
        }
        // 프로젝트 미존재 시
        else {

            // 2. 로컬 DB에 해당 고객사가 존재하는지 조회
            // 고객사 존재 시 업데이트 후 지원내역에 저장
            if (clientRepository.existsByCompanyGuid(licenseProject.getCompany().getCompanyGuid())) {
                Client newClient = clientRepository.findFirstByCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setName(licenseProject.getCompany().getCompanyName());

                clientRepository.save(newClient);
            }
            // 고객사 미존재 시 새로운 객체 생성 후 저장
            else{
                Client newClient = new Client();
                newClient.setName(licenseProject.getCompany().getCompanyName());
                newClient.setCompanyGuid(licenseProject.getCompany().getCompanyGuid());
                newClient.setCompanyRegDate(licenseProject.getCompany().getCompanyRegDate());

                clientRepository.save(newClient);
                log.info("Saved Client :: " + newClient.getName());
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

        return newsupport;
    }


    /**
     * [ 지원내역 삭제 ]
     * @param supportId
     */
    public void deleteSupport(Long supportId) {

        Memp user = mempRepository.findFirstByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 해당 지원내역에 첨부된 파일 전부 삭제
        fileRepository.deleteAllBySupportId(supportId);

        // 작성자와 삭제요청자가 동일하거나, 유저가 SUPERADMIN 권한을 가졌을 때 지원내역 삭제
        if (support != null && (support.getEngineer().getUsername().equals(user.getUsername())
                || user.getAuthorities().contains(Authority.of("ROLE_SUPERADMIN")))){
            supportRepository.delete(support);
            log.info("===DELETE=== ("+ SupportResponse.entityToResponse(support) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());
        }

    }

    /**
     * [ 지원 내역 첨부 파일 저장 ]
     * @param files
     * @param supportId
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private void saveFile(List<MultipartFile> files, Long supportId) throws NoSuchAlgorithmException, IOException {
        List<File> savedFiles = new ArrayList<>();

        for(MultipartFile file : files) {
            if (Objects.requireNonNull(file.getOriginalFilename()).isEmpty())
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

    }

}
