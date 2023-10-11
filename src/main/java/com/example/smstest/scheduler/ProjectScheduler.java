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

//    @Scheduled(fixedDelay = 1000000)
    @Scheduled(cron = "0 0 0,6,12,18 * * ?") // 매일 06시, 12시, 18시, 24시 실행
    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {

        List<LicenseProject> licenseProjects = licenseProjectRepository.findAll();

        for (LicenseProject projects : licenseProjects) {
            if (!clientRepository.existsByName(projects.getCompanyName())) {
                Client newClient = new Client();
                newClient.setName(projects.getCompanyName());
                clientRepository.save(newClient);
                System.out.println("====Save Client :: "+ newClient.getName()+" ====");

            }

            if (!projectRepository.existsByUniqueCode(projects.getProjectCode())){
                Client client = clientRepository.findOneByName(projects.getCompanyName());
                Project project = Project.builder()
                        .client(client)
                        .name(projects.getProjectName())
                        .uniqueCode(projects.getProjectCode())
                        .startDate(projects.getLicenseStartDate())
                        .finishDate(projects.getLicenseEndDate())
                        .build();
                projectRepository.save(project);
                System.out.println("====Save Project :: "+ project.getName()+" ("+project.getUniqueCode()+")====");
            }
        }

    }
}