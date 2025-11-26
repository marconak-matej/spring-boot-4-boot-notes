package io.github.mm.flyway.product.rest.dto;

import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Product response")
public record ProductResponse(
        @Schema(description = "Product ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "Product name",
                example = "Laptop",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 200)
        String name,

        @Schema(description = "Product description", example = "High-performance laptop", maxLength = 5000)
        String description,

        @Schema(
                description = "Product price",
                example = "1299.99",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0.01",
                maximum = "99999999.99")
        BigDecimal price,

        @Schema(description = "Stock quantity", example = "100", minimum = "0")
        Integer stockQuantity,

        @Schema(description = "Product SKU (Stock Keeping Unit)", example = "PROD-00001", maxLength = 50)
        String sku,

        @Schema(description = "Product category", example = "ELECTRONICS")
        ProductCategory category,

        @Schema(description = "Product status", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
        ProductStatus status,

        @Schema(description = "Timestamp when the product was created", example = "2024-11-26T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the product was last updated", example = "2024-11-26T12:30:00Z")
        Instant updatedAt) {}
