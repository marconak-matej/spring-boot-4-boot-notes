package io.github.mm.http.exchange.config;

import io.github.mm.http.exchange.product.client.ProductService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(
        group = "product",
        types = {ProductService.class})
public class HttpServiceConfig {}
