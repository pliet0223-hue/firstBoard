package com.example.newboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(auth -> auth
                // public: home, login, join, static assets
                .requestMatchers("/", "/login", "/join",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // anyone can READ articles (GET)
                .requestMatchers(HttpMethod.GET, "/articles", "/articles/*").permitAll()
                // write operations require login
                .requestMatchers("/articles/new", "/articles/*/edit").authenticated()
                .requestMatchers(HttpMethod.POST, "/articles/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/articles/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/articles/**").authenticated()
                // comments api: create/delete require login, list read permitted by controller
                .requestMatchers(HttpMethod.POST, "/api/articles/*/comments").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/comments/*").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
