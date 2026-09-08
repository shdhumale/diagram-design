package com.ruflo.ticketing.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Management API")
                        .version("v1")
                        .description("Spring Boot 3 backend for ticket management"))
                .externalDocs(new ExternalDocumentation()
                        .description("Product docs")
                        .url("/document/PRD.md"));
    }
}
