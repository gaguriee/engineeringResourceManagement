package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.employee.entity.Employee;
import com.example.smstest.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;

    private final EmployeeRepository employeeRepository;

//    private final PasswordEncoder passwordEncoder;

    @Value("${somansa.password}")
    private String basicPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByUserid(username);

        if (employee == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Memp> memp = mempRepository.findByUsername(username);

        /**
         * tb_mempdata에는 있지만 erm db에는 없는 경우 신규 등록
         */
        Random rand = new Random();
        String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));

        if (memp.isEmpty()) {
            memp = Optional.ofNullable(Memp.builder()
                    .name(employee.getEmpname())
                    .rank("엔지니어")
                    .position("팀원")
                    .team(teamRepository.findByName(employee.getDepartment().getDeptname()))
                    .username(employee.getUserid())
                    .password(basicPassword)
                    .calenderColor(randomColor)
                    .authorities(authoritiesSet)
                    .build());
//            memp.encodePassword(passwordEncoder);

            try{
                mempRepository.save(memp.get());
            }
            catch (Exception e){
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }

        return new User(memp.get().getUsername(), memp.get().getPassword(),
                memp.get().getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toSet()));
    }
}
