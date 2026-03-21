package com.buildsmart.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi projectManagerApis() {
        return GroupedOpenApi.builder()
                .group("Project Manager APIs")
                .pathsToMatch("/api/project-manager/**")
                .build();
    }

    @Bean
    public GroupedOpenApi financeApis() {
        return GroupedOpenApi.builder()
                .group("Finance APIs")
                .pathsToMatch("/api/finance/**")
                .build();
    }
}
