package io.github.mm.flyway.product.fixtures;

import io.github.mm.flyway.product.domain.Product;
import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import java.math.BigDecimal;
import java.time.Instant;

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
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                "test",
                "test");
    }

    public static Product mouse() {
        return new Product(
                null,
                "Wireless Mouse",
                "Ergonomic wireless mouse",
                new BigDecimal("29.99"),
                200,
                "PROD-MOUSE-001",
                ProductCategory.PERIPHERALS,
                ProductStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                "test",
                "test");
    }

    public static Product monitor() {
        return new Product(
                null,
                "4K Monitor",
                "27-inch 4K UHD Monitor",
                new BigDecimal("599.99"),
                30,
                "PROD-MONITOR-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                "test",
                "test");
    }

    public static Product webcam() {
        return new Product(
                null,
                "HD Webcam",
                "1080p webcam with microphone",
                new BigDecimal("79.99"),
                75,
                "PROD-WEBCAM-001",
                ProductCategory.PERIPHERALS,
                ProductStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                "test",
                "test");
    }

    public static Product inactiveProduct() {
        return new Product(
                null,
                "Discontinued Mouse",
                "Old model mouse",
                new BigDecimal("19.99"),
                0,
                "PROD-OLD-001",
                ProductCategory.PERIPHERALS,
                ProductStatus.INACTIVE,
                Instant.now(),
                Instant.now(),
                "test",
                "test");
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
