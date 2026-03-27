package com.buildsmart.common.config;

import com.buildsmart.iam.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Map;

/**
 * Security config for projectmanager, finance, and notification APIs.
 * Reuses IAM's JwtAuthenticationFilter. Custom error messages for auth failures.
 */
@Configuration
@EnableWebSecurity
public class ModuleSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain moduleFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                                 CorsConfigurationSource corsConfigurationSource,
                                                 ObjectMapper objectMapper) throws Exception {
        http
            .securityMatcher("/api/project-manager/**", "/api/finance/**", "/api/notifications/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/project-manager/**").hasAnyRole("ADMIN", "PROJECT_MANAGER", "SITE_ENGINEER")
                .requestMatchers("/api/finance/**").hasAnyRole("ADMIN", "FINANCE_OFFICER")
                .requestMatchers("/api/notifications/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    String message = req.getHeader("Authorization") == null || !req.getHeader("Authorization").startsWith("Bearer ")
                        ? "Please login first"
                        : "Session Expired. Please login again.";
                    Map<String, Object> body = Map.of(
                        "timestamp", java.time.Instant.now().toString(),
                        "status", 401,
                        "error", "Unauthorized",
                        "message", message
                    );
                    res.getWriter().write(objectMapper.writeValueAsString(body));
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> body = Map.of(
                        "timestamp", java.time.Instant.now().toString(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", "Access Denied: Insufficient Role"
                    );
                    res.getWriter().write(objectMapper.writeValueAsString(body));
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
