package com.example.smstest.global.config;

import com.example.smstest.domain.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

/**
 * Spring Security 설정 파일
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                // 로그인, 로그아웃 설정
                .formLogin()
                .loginPage("/account/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutSuccessUrl("/account/login")
                .and()

                // 앤드포인트 별 접근 권한 제어
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/templates/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/static/favicon.ico").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/account/login").permitAll()
                .antMatchers("/account/register").hasAnyRole("ADMIN", "SUPERADMIN")
                .antMatchers("/performance/inquiry").hasAnyRole("ADMIN", "SUPERADMIN")
                .anyRequest().authenticated()
                .and().logout().permitAll()
                .and()

                // 아이디를 쿠키에 저장 (자동 로그인)
                .rememberMe()
                .key("somansa!")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(604800) // 쿠키의 만료시간 설정(초), default: 14일
                .alwaysRemember(false) // 사용자가 체크박스를 활성화하지 않아도 항상 실행, default: false

                // 사용자 인증 인가에 관여하는 Service
                .userDetailsService(customUserDetailsService);
    }

    // Thymeleaf에서 sec 속성을 사용할 수 있다. (인증정보에 직접 접근해서 인가를 할 수 있음)
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8"); // 파일 인코딩 설정
        multipartResolver.setMaxUploadSizePerFile(50 * 1024 * 1024); // 파일당 업로드 크기 제한 (50MB)
        return multipartResolver;
    }
}