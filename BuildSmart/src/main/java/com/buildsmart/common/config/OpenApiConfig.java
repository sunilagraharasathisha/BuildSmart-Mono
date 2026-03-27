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

    @Value("${app.docs.server.dev:http://localhost:8082}")
    private String devServer;

    @Value("${app.docs.server.prod:https://api.buildsmart.com}")
    private String prodServer;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildSmart API")
                        .description("""
                                APIs for BuildSmart Construction Project Planning & Site Operations Management System.
                                Modules: IAM, Project Management, Finance, Safety, Notifications.
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

    @Bean
    public GroupedOpenApi iamApis() {
        return GroupedOpenApi.builder()
                .group("IAM")
                .pathsToMatch("/admin/**", "/users/**", "/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi projectManagerApis() {
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
    public GroupedOpenApi safetyApis() {
        return GroupedOpenApi.builder()
                .group("Safety")
                .pathsToMatch("/api/v1/safety/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApis() {
        return GroupedOpenApi.builder()
                .group("Notifications")
                .pathsToMatch("/api/notifications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi vendorApis() {
        return GroupedOpenApi.builder()
                .group("Vendor")
                .pathsToMatch("/api/vendors/**", "/api/vendor/invoices/**", "/api/contracts/**", "/api/deliveries/**")
                .build();
    }

    @Bean
    public GroupedOpenApi resourceApis() {
        return GroupedOpenApi.builder()
                .group("Resource Allocation")
                .pathsToMatch("/api/resources/**", "/api/allocations/**")
                .build();
    }

    @Bean
    public GroupedOpenApi siteOpsApis() {
        return GroupedOpenApi.builder()
                .group("Site Operations")
                .pathsToMatch("/api/sitelogs/**", "/api/issues/**", "/api/resource-requests/**")
                .build();
    }
}
