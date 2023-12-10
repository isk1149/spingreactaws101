package com.example.spingreactaws101.config;

import com.example.spingreactaws101.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    //서블릿컨테이너에게 이 서블릿 필터를 사용하라고 알려주는 작업

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //http시큐리티 빌더
        http.cors()//WebMfcConfig에서 이미 설정했으므로 기본 cors 설정
                .and()
                .csrf()//csft는 현재 사용하지 않으므로 disable
                .disable()
                .httpBasic()//token을 사용하므로 basic인증 disable
                .disable()
                .sessionManagement()//session기반이 아님을 선언
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()// /와 /auth/** 경로는 인증 안 해도 됨
                .antMatchers("/","/auth/**").permitAll()
                .anyRequest()// /와 /auth/** 이외의 모든 경로는 인증해야됨
                .authenticated();
        //filter등록
        //매 요청마다 CorsFilter 실행한 후에 jwtAuthenticationFilter를 실행한다
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class );
        return http.build();
    }
}