package com.example.society.config;

import com.example.society.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean(name = "mainFilterChain")
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                    "/admin/css/**", "/admin/js/**", "/admin/images/**",
                    "/api/visitor/approve",

                    // Public APIs (e.g., login, OTP)
                    "/guard/auth/**",
                    "/residence/auth/**",   // <-- add this
                    "/api/admin/login/**",
                    "/api/test/generate",

                    // Admin panel HTML views
                    "/admin/login",
                    "/admin/dashboard",
                    "/admin/visitors",
                    "/admin/residences",
                    "/admin/residences/register"
                ).permitAll()


                // Admin APIs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Mobile User APIs
                .requestMatchers(
                    "/user/**",
                    "/api/guest/**",
                    "/api/residences"
                ).authenticated()

                // Visitor API
                .requestMatchers("/api/visitor").authenticated()

                // Everything else
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Custom error handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized - Please login\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Forbidden - You don’t have permission\"}");
                })
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean(name = "mainPasswordEncoder")
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
