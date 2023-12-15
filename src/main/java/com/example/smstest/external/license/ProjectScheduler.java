package com.example.smstest.external.license;

import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.project.Project;
import com.example.smstest.domain.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * 라이센스 DB에서 프로젝트를 주기적으로 가져오는 스케줄러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectScheduler {

    private final LicenseProjectRepository licenseProjectRepository;
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

//    @Scheduled(fixedDelay = 1000000000)
    @Scheduled(cron = "0 0 0/6 * * ?") // 매일 6시간 간격으로 실행
    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {

        log.info("=====START SCHEDULER=====");

        try {
            List<LicenseProject> licenseProjects = licenseProjectRepository.findAll();


            for (LicenseProject licenseProject : licenseProjects) {

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

                // 라이센스 DB에서 가져온 project 중 기존의 로컬 DB에 존재하지 않는 project일 경우 새로 저장
                if (!projectRepository.existsByUniqueCode(licenseProject.getProjectCode())) {
                    Project project = Project.builder()
                            .client(client)
                            .name(licenseProject.getProjectName())
                            .uniqueCode(licenseProject.getProjectCode())
                            .projectRegDate(licenseProject.getProjectRegDate())
                            .projectGuid(licenseProject.getProjectGuid())
                            .build();
                    projectRepository.save(project);
                    log.info("Saved Project :: " + project.getName() + " (" + project.getUniqueCode() + ")");
                }
                // 기존의 로컬 DB에 존재하는 project일 경우 업데이트
                else {
                    Project project = projectRepository.findFirstByUniqueCode(licenseProject.getProjectCode());
                    project.updateProject(
                            licenseProject.getProjectName()
                    );
                    projectRepository.save(project);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred in scheduleGetInitialFiles: " + e.getMessage());
        }
    }
}