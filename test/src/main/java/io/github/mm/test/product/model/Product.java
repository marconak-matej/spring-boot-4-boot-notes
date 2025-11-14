package io.github.mm.test.product.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product entity representing an item in the catalog")
public record Product(
        @Schema(
                description = "Unique identifier for the product",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Product name", example = "Laptop", required = true)
        String name,

        @Schema(description = "Product price in USD", example = "999.99", required = true, minimum = "0")
        Double price) {

    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }
}
