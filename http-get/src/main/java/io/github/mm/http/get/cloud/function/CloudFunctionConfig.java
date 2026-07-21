package io.github.mm.http.get.cloud.function;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudFunctionConfig {

    @Bean("hello-supplier")
    public Supplier<String> helloSupplier() {
        return () -> "Hello from Spring Cloud Function";
    }
}
