package io.github.mm.http.client.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI httpClientOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HTTP Client Demo API")
                        .description(
                                "Demonstrates different HTTP client approaches in Spring Boot 4.0: RestClient, WebClient, and RestTemplate")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Spring Boot 4.0 Demo")
                                .url("https://github.com/mm/boot-notes")
                                .email("contact@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
