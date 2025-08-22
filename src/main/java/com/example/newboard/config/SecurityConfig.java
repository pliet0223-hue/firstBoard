package com.example.newboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/articles", "/articles/**",
                                 "/login", "/join",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/articles/new", "/articles/*/edit", "/articles/*/delete").authenticated()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/articles", false)
                .failureUrl("/login?error")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}
