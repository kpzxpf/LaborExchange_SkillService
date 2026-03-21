package com.volzhin.laborexchange_skillservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Skill Service API")
                        .version("1.0.0")
                        .description("""
                                Central skill dictionary used by both Vacancy Service and Resume Service.

                                **Caching:** All skill lookups are cached in Redis (TTL: 2 hours).

                                **Bulk endpoint** `GET /api/skills/names/by-ids` is called internally by \
VacancyService and ResumeService when building Elasticsearch index events.

                                **Create is idempotent:** POST returns the existing skill if the name \
already exists (case-insensitive comparison).

                                **Database:** PostgreSQL (`skilldb`, port 5438).
                                """)
                        .license(new License().name("MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained via POST /api/auth/login")))
                .servers(List.of(
                        new Server().url("http://localhost:8086").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Skills").description("Skill dictionary CRUD and bulk name lookup")));
    }
}
