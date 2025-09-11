package com.udacity.jwdnd.course1.cloudstorage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // re-enable later and add CSRF fields in forms
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login", "/signup",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")          // GET /login renders your page
                        .loginProcessingUrl("/login") // POST /login handled by Spring Security
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}

