package io.github.mm.flyway.product.rest.dto;

import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Product creation/update request")
public record ProductRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 200, message = "Name must be between 1 and 200 characters")
        @Schema(
                description = "Product name",
                example = "Laptop",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1,
                maxLength = 200)
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        @Schema(description = "Product description", example = "High-performance laptop", maxLength = 5000)
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Price must not exceed 99,999,999.99")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimal places")
        @Schema(
                description = "Product price",
                example = "1299.99",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0.01",
                maximum = "99999999.99")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        @Schema(description = "Stock quantity", example = "100", minimum = "0")
        Integer stockQuantity,

        @Size(max = 50, message = "SKU must not exceed 50 characters")
        @Schema(description = "Product SKU (Stock Keeping Unit)", example = "PROD-00001", maxLength = 50)
        String sku,

        @Schema(description = "Product category", example = "ELECTRONICS")
        ProductCategory category,

        @Schema(description = "Product status", example = "ACTIVE")
        ProductStatus status) {}
