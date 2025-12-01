package io.github.mm.jooq.test;

import io.github.mm.jooq.config.TestcontainersConfiguration;
import java.lang.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public @interface SpringBootIntegrationTest {}
