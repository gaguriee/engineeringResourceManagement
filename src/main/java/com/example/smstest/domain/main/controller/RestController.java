package com.example.smstest.domain.main.controller;

import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.dto.ProjectRequest;
import com.example.smstest.domain.project.dto.CreateProjectResponse;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RestController
public class RestController {
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;

    @PostMapping("/project/create")
    public CreateProjectResponse createCustomer(@RequestBody ProjectRequest request) {

        boolean clientExist = clientRepository.existsByName(request.getClientName());
        if (!clientExist){
            Client client = new Client();
            client.setName(request.getClientName());
            clientRepository.save(client);        }

        Client client = clientRepository.findOneByName(request.getClientName());

        boolean projectExist = projectRepository.existsByUniqueCode(request.getUniqueCode());

        if (!projectExist){
            Project project = Project.builder()
                    .name(request.getProjectName())
                    .client(client)
                    .product(productRepository.findById(request.getProductId()).get())
                    .team(teamRepository.findById(request.getTeamId()).get())
                    .startDate(request.getStartDate())
                    .finishDate(request.getFinishDate())
                    .engineer(mempRepository.findOneByName(request.getEngineerName())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                    .subEngineer(mempRepository.findOneByName(request.getSubEngineerName())
                            .orElse(null))
                    .uniqueCode(request.getUniqueCode())
                    .build();

            Project newProject = projectRepository.save(project);

            if (newProject.getId() != null) {
                return new CreateProjectResponse(newProject.getId(), false);
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }
        else {
            return new CreateProjectResponse(0L, true);
        }

    }

    @PostMapping("/project/verify")
    public CreateProjectResponse verifyProject(@RequestBody ProjectRequest request) {

        boolean clientExist = clientRepository.existsByName(request.getClientName());
        if (!clientExist){
            Client client = new Client();
            client.setName(request.getClientName());
            clientRepository.save(client);        }

        Client client = clientRepository.findOneByName(request.getClientName());

        boolean projectExist = projectRepository.existsByUniqueCode(request.getUniqueCode());

        if (!projectExist){
            Project project = Project.builder()
                    .name(request.getProjectName())
                    .client(client)
                    .product(productRepository.findById(request.getProductId()).get())
                    .team(teamRepository.findById(request.getTeamId()).get())
                    .startDate(request.getStartDate())
                    .finishDate(request.getFinishDate())
                    .engineer(mempRepository.findOneByName(request.getEngineerName())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                    .subEngineer(mempRepository.findOneByName(request.getSubEngineerName())
                            .orElse(null))
                    .uniqueCode(request.getUniqueCode())
                    .build();

            Project newProject = projectRepository.save(project);

            if (newProject.getId() != null) {
                return new CreateProjectResponse(newProject.getId(), false);
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }
        else {
            return new CreateProjectResponse(0L, true);
        }


    }


}