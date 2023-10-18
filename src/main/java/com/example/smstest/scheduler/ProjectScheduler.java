package com.example.smstest.scheduler;

import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.scheduler.entity.LicenseProject;
import com.example.smstest.scheduler.repository.LicenseProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectScheduler {

    private final LicenseProjectRepository licenseProjectRepository;
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    @Scheduled(cron = "0 0 0,6,12,18 * * ?") // 매일 06시, 12시, 18시, 24시 실행
    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {

        try {
            List<LicenseProject> licenseProjects = licenseProjectRepository.findAll();


            for (LicenseProject licenseProject : licenseProjects) {

                if (!clientRepository.existsByName(licenseProject.getCompanyName())) {
                    Client newClient = new Client();
                    newClient.setName(licenseProject.getCompanyName());
                    clientRepository.save(newClient);
                    log.info("Saved Client :: " + newClient.getName());
                }

                Client client = clientRepository.findOneByName(licenseProject.getCompanyName());

                // 라이센스 DB에서 가져온 project 중 기존의 로컬 DB에 존재하지 않는 project일 경우 새로 저장
                if (!projectRepository.existsByUniqueCode(licenseProject.getProjectCode())) {
                    Project project = Project.builder()
                            .client(client)
                            .name(licenseProject.getProjectName())
                            .uniqueCode(licenseProject.getProjectCode())
                            .startDate(licenseProject.getLicenseStartDate())
                            .finishDate(licenseProject.getLicenseEndDate())
                            .build();
                    projectRepository.save(project);
                    log.info("Saved Project :: " + project.getName() + " (" + project.getUniqueCode() + ")");
                }
                // 기존의 로컬 DB에 존재하는 project일 경우 업데이트
                else {
                    Project project = projectRepository.findByUniqueCode(licenseProject.getProjectCode());
                    project.updateProject(
                            licenseProject.getProjectName(),
                            client,
                            licenseProject.getLicenseStartDate(),
                            licenseProject.getLicenseEndDate()
                    );
                    projectRepository.save(project);

                }
            }
        } catch (Exception e) {
            log.error("Error occurred in scheduleGetInitialFiles: " + e.getMessage());
        }
    }
}