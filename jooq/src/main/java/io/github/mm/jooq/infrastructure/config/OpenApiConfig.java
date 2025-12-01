package io.github.mm.jooq.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("jOOQ Product Management API")
                        .version("1.0.0")
                        .description("""
                                RESTful API for product catalog management demonstrating Spring Boot 4.0 with jOOQ integration.

                                ## Features
                                * Full CRUD operations for products
                                * Pagination and filtering support
                                * Type-safe database queries with jOOQ
                                * Database migrations with Flyway
                                * PostgreSQL as the database backend

                                ## Technical Stack
                                * Spring Boot 4.0
                                * jOOQ for type-safe SQL
                                * Flyway for database migrations
                                * PostgreSQL 16
                                * Testcontainers for testing
                                """)
                        .contact(new Contact()
                                .name("Spring Boot 4.0 Boot Notes")
                                .url("https://github.com/mm/spring-boot-4-boot-notes")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.example.com").description("Production server")));
    }
}
