package io.github.mm.test.product.model;

public record Product(Long id, String name, Double price) {

    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }
}
