package com.buildsmart.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Optional: allow overriding via properties
    @Value("${app.docs.server.dev:http://localhost:8082}")
    private String devServer;

    @Value("${app.docs.server.prod:https://api.buildsmart.com}")
    private String prodServer;

    /** Single, authoritative OpenAPI bean (applies to all groups). */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildSmart API")
                        .description("""
                                APIs for BuildSmart Construction Project Planning & Site Operations Management System.
                                Modules: IAM, Project Management, Finance, Notifications.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BuildSmart Development Team")
                                .email("dev@buildsmart.com")
                                .url("https://buildsmart.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server().url(devServer).description("Development Server"))
                .addServersItem(new Server().url(prodServer).description("Production Server"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication token. Format: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    /** Groups (adjust paths to match your controllers). */
    @Bean
    public GroupedOpenApi iamApis() {
        // If your IAM endpoints are actually under /api/v1/auth/**
        return GroupedOpenApi.builder()
                .group("IAM")
                .pathsToMatch("/admin/**", "/users/**", "/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi projectManagerApis() {
        // Your sample path looked like /api/v1/api/project-manager/...
        // Suggest normalizing to /api/v1/project-manager/** in controllers & security
        return GroupedOpenApi.builder()
                .group("Project Manager")
                .pathsToMatch("/api/project-manager/**")
                .build();
    }

    @Bean
    public GroupedOpenApi financeApis() {
        return GroupedOpenApi.builder()
                .group("Finance")
                .pathsToMatch("/api/finance/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApis() {
        return GroupedOpenApi.builder()
                .group("Notifications")
                .pathsToMatch("/api/notifications/**")
                .build();
    }
}