package io.github.mm.jooq.product.fixtures;

import io.github.mm.jooq.product.rest.dto.Product;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProductFixtures {

    private ProductFixtures() {}

    public static Product laptop() {
        return new Product(
                null,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("1299.99"),
                50,
                "PROD-LAPTOP-001",
                "ELECTRONICS",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                "test",
                null);
    }

    public static Product mouse() {
        return new Product(
                null,
                "Wireless Mouse",
                "Ergonomic wireless mouse",
                new BigDecimal("29.99"),
                200,
                "PROD-MOUSE-001",
                "PERIPHERALS",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                "test",
                null);
    }

    public static Product monitor() {
        return new Product(
                null,
                "4K Monitor",
                "27-inch 4K UHD Monitor",
                new BigDecimal("599.99"),
                30,
                "PROD-MONITOR-001",
                "ELECTRONICS",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                "test",
                null);
    }

    public static Product webcam() {
        return new Product(
                null,
                "HD Webcam",
                "1080p webcam with microphone",
                new BigDecimal("79.99"),
                75,
                "PROD-WEBCAM-001",
                "PERIPHERALS",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                "test",
                null);
    }

    public static Product inactiveProduct() {
        return new Product(
                null,
                "Discontinued Mouse",
                "Old model mouse",
                new BigDecimal("19.99"),
                0,
                "PROD-OLD-001",
                "PERIPHERALS",
                ProductStatus.INACTIVE,
                LocalDateTime.now(),
                null,
                "test",
                null);
    }

    public static Product withId(Product product, Long id) {
        return new Product(
                id,
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity(),
                product.sku(),
                product.category(),
                product.status(),
                product.createdAt(),
                product.updatedAt(),
                product.createdBy(),
                product.updatedBy());
    }
}
