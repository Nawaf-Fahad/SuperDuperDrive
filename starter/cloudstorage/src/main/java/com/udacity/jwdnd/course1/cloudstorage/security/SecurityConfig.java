package com.udacity.jwdnd.course1.cloudstorage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // or keep enabled + CSRF hidden input
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signup", "/login",
                                "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                );
        return http.build();
    }

}
