package io.github.mm.test.config;

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
    public OpenAPI testModuleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot 4.0 Test Module API")
                        .version("1.0.0")
                        .description("""
                                Comprehensive REST API demonstrating Spring Boot 4.0 testing capabilities including:
                                - JUnit Jupiter 6 integration
                                - RestTestClient for modern API testing
                                - AssertJ for fluent assertions
                                - Testcontainers integration

                                This API provides product management endpoints for educational purposes.
                                """)
                        .contact(new Contact()
                                .name("Spring Boot 4.0 Demo")
                                .url("https://github.com/mm/boot-notes")
                                .email("demo@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server"),
                        new Server().url("https://api.example.com").description("Production Server")));
    }
}
