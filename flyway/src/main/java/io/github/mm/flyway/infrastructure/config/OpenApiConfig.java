package io.github.mm.flyway.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flywayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flyway Database Migrations API")
                        .description("""
                                Spring Boot 4.0 demonstration of Flyway database migrations with PostgreSQL.

                                ### Pagination
                                This API supports pagination for list endpoints using the following query parameters:
                                - `page`: Page number (0-indexed, default: 0)
                                - `size`: Number of items per page (default: 20, max: 100)
                                - `sort`: Sort criteria in format `property,direction` (e.g., `name,asc`)

                                Example: `/api/products?page=0&size=20&sort=name,asc`
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Spring Boot 4.0 Notes").url("https://github.com/mm/boot-notes"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components().addSchemas("PageMetadata", createPageMetadataSchema()));
    }

    private Schema<?> createPageMetadataSchema() {
        return new ObjectSchema()
                .description("Pagination metadata")
                .addProperty(
                        "size",
                        new IntegerSchema()
                                .description("Number of items per page")
                                .example(20))
                .addProperty(
                        "number",
                        new IntegerSchema()
                                .description("Current page number (0-indexed)")
                                .example(0))
                .addProperty(
                        "totalElements",
                        new IntegerSchema()
                                .description("Total number of items across all pages")
                                .example(100))
                .addProperty(
                        "totalPages",
                        new IntegerSchema().description("Total number of pages").example(5));
    }
}
