package com.example.smstest.config;

import com.example.smstest.domain.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

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
                .formLogin()
                .loginPage("/account/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutSuccessUrl("/account/login")
                .and()
                .authorizeRequests()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/templates/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/static/favicon.ico").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/account/login").permitAll()
                .antMatchers("/account/register").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().logout().permitAll()
                .and()
                .rememberMe()
                .key("somansa!")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(604800) // 쿠키의 만료시간 설정(초), default: 14일
                .alwaysRemember(false) // 사용자가 체크박스를 활성화하지 않아도 항상 실행, default: false
                .userDetailsService(customUserDetailsService); // 기능을 사용할 때 사용자 정보가 필요함. 반드시 이 설정 필요함.
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }

}