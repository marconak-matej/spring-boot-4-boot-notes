package io.github.mm.http.exchange.product.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Product entity with dynamic data attributes")
public record Product(
        @Schema(description = "Unique product identifier", example = "prod-123")
        String id,

        @Schema(description = "Product name", example = "Coffee Maker")
        String name,

        @Schema(description = "Dynamic product attributes") Map<String, Object> data) {}
