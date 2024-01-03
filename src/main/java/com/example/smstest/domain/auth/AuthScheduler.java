package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.external.employee.Employee;
import com.example.smstest.external.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthScheduler {

    private final MempRepository mempRepository;
    private final EmployeeRepository employeeRepository;

    @Scheduled(cron = "0 0 9 * * MON")// 매주 월요일 아침 9시에 탈퇴 유저 체크
    public void deactivateUsersWithStatusZero() {
        List<Memp> memps = mempRepository.findAllByActiveTrue();
        for (Memp memp : memps) {
            Optional<Employee> employee= employeeRepository.findFirstByUserid(memp.getUsername());
            if (employee.isPresent()){
                int userStatus = employee.get().getUserstatus();
                if (userStatus == 0) {
                    memp.setActive(false);
                    mempRepository.save(memp);
                    log.info("Deactivate User :: "+ memp);
                }
            }
        }
    }
}
