package io.github.mm.jooq.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Product entity representing a catalog item")
public record Product(
        @Schema(
                description = "Unique identifier of the product",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Name of the product", example = "Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Detailed description of the product", example = "High-performance laptop with 16GB RAM")
        String description,

        @Schema(description = "Price of the product", example = "1299.99", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @Schema(description = "Available stock quantity", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stockQuantity,

        @Schema(
                description = "Stock Keeping Unit - unique product identifier",
                example = "LAP-001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String sku,

        @Schema(description = "Product category", example = "Electronics")
        String category,

        @Schema(description = "Current status of the product")
        ProductStatus status,

        @Schema(
                description = "Timestamp when the product was created",
                example = "2024-01-01T10:00:00",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt,

        @Schema(
                description = "Timestamp when the product was last updated",
                example = "2024-01-15T14:30:00",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime updatedAt,

        @Schema(
                description = "User who created the product",
                example = "admin",
                accessMode = Schema.AccessMode.READ_ONLY)
        String createdBy,

        @Schema(
                description = "User who last updated the product",
                example = "admin",
                accessMode = Schema.AccessMode.READ_ONLY)
        String updatedBy) {}
