package io.github.mm.flyway.product.domain;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public record Product(
        @Id Long id,

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 200, message = "Name must be between 1 and 200 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @DecimalMax(value = "99999999.99", message = "Price must not exceed 99,999,999.99")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits and 2 decimal places")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative") @Column("stock_quantity")
        Integer stockQuantity,

        @Size(max = 50, message = "SKU must not exceed 50 characters")
        String sku,

        ProductCategory category,

        @NotNull(message = "Status is required") ProductStatus status,

        @Column("created_at") Instant createdAt,
        @Column("updated_at") Instant updatedAt,

        @Column("created_by") @Size(max = 100, message = "Created by must not exceed 100 characters")
        String createdBy,

        @Column("updated_by") @Size(max = 100, message = "Updated by must not exceed 100 characters")
        String updatedBy) {

    public Product withUpdatedFields(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            String sku,
            ProductCategory category,
            ProductStatus status,
            Instant updatedAt,
            String updatedBy) {
        return new Product(
                id,
                name,
                description,
                price,
                stockQuantity,
                sku,
                category,
                status,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy);
    }

    public Product withAuditFields(Instant now, String username) {
        return new Product(
                id, name, description, price, stockQuantity, sku, category, status, now, now, username, username);
    }
}
