package io.github.mm.http.get.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI httpGetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HTTP GET API")
                        .description("Ways to handle HTTP GET in Spring Boot")
                        .version("1.0")
                        .contact(new Contact().name("mm"))
                        .license(new License().name("MIT")));
    }
}
