package io.github.mm.flyway.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackages = "io.github.mm.flyway.product.repository")
public class JdbcConfig {}
