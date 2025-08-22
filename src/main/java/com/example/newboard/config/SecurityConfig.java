package com.example.newboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // /api/** 는 csrf 제외
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능한 경로
                        .requestMatchers("/", "/articles", "/articles/**",
                                "/login", "/join",
                                "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // 인증 필요
                        .requestMatchers("/articles/new", "/articles/*/edit", "/articles/*/delete").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        // 나머지 요청은 전부 인증
                        .anyRequest().authenticated()
                )
                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        // 저장된 요청 있으면 그리로, 없으면 /articles
                        .defaultSuccessUrl("/articles", false)
                        .failureUrl("/login?error")
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")   // 반드시 POST
                        .logoutSuccessUrl("/")
                )
                // OAuth2 (구글/깃허브 등)
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }

    // 🔑 PasswordEncoder 빈 등록 (UserService에서 필요)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}