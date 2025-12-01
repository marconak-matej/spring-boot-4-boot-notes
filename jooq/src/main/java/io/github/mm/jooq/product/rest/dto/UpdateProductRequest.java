package io.github.mm.jooq.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Schema(description = "Request payload for updating an existing product. All fields are optional.")
public record UpdateProductRequest(
        @Schema(description = "Updated name of the product", example = "Gaming Laptop")
        String name,

        @Schema(
                description = "Updated description of the product",
                example = "High-performance gaming laptop with RTX 4060")
        String description,

        @Schema(description = "Updated price of the product", example = "1599.99")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Updated stock quantity", example = "30")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @Schema(description = "Updated SKU", example = "LAP-001-V2")
        String sku,

        @Schema(description = "Updated product category", example = "Gaming")
        String category,

        @Schema(description = "Updated status of the product")
        ProductStatus status) {}
