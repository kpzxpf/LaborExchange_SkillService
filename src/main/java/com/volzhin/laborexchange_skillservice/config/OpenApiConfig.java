package com.volzhin.laborexchange_skillservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                                Central skill dictionary for LaborExchange.

                                Skills are used by both vacancies and resumes. Lookups are cached in Redis (TTL: 2 hours).

                                **Bulk endpoint** `GET /api/skills/names/by-ids` is called internally by VacancyService and ResumeService when building Elasticsearch index events.
                                """)
                        .contact(new Contact().name("LaborExchange Team"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8086").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Skills").description("Skill dictionary CRUD and bulk name lookup")));
    }
}
