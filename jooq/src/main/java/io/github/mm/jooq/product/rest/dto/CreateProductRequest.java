package io.github.mm.jooq.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Request payload for creating a new product")
public record CreateProductRequest(
        @Schema(description = "Name of the product", example = "Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Product name is required")
        String name,

        @Schema(description = "Detailed description of the product", example = "High-performance laptop with 16GB RAM")
        String description,

        @Schema(description = "Price of the product", example = "1299.99", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Initial stock quantity", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @Schema(
                description = "Stock Keeping Unit - unique product identifier",
                example = "LAP-001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "SKU is required")
        String sku,

        @Schema(description = "Product category", example = "Electronics")
        String category,

        @Schema(description = "Initial status of the product", defaultValue = "ACTIVE")
        ProductStatus status) {}
