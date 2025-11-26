package io.github.mm.flyway.product.fixtures;

import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import io.github.mm.flyway.product.rest.dto.ProductRequest;
import java.math.BigDecimal;

public final class ProductRequestFixtures {

    private ProductRequestFixtures() {}

    public static ProductRequest reqMonitor() {
        return new ProductRequest(
                "Monitor",
                "4K UHD Monitor",
                new BigDecimal("599.99"),
                50,
                "PROD-MONITOR-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withStatus(ProductStatus status) {
        return new ProductRequest(
                "Test Product",
                "Test description",
                new BigDecimal("99.99"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                status);
    }

    public static ProductRequest minimal() {
        return new ProductRequest(
                "Minimal Product", null, new BigDecimal("9.99"), null, null, null, ProductStatus.ACTIVE);
    }

    public static ProductRequest withTooLongName() {
        return new ProductRequest(
                "A".repeat(201),
                "Valid description",
                new BigDecimal("99.99"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withEmptyName() {
        return new ProductRequest(
                "",
                "Valid description",
                new BigDecimal("99.99"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withNegativePrice() {
        return new ProductRequest(
                "Valid Name",
                "Valid description",
                new BigDecimal("-10.00"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withZeroPrice() {
        return new ProductRequest(
                "Valid Name",
                "Valid description",
                new BigDecimal("0.00"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withExcessivePrice() {
        return new ProductRequest(
                "Valid Name",
                "Valid description",
                new BigDecimal("999999999.99"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withNegativeStock() {
        return new ProductRequest(
                "Valid Name",
                "Valid description",
                new BigDecimal("99.99"),
                -1,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withTooLongSku() {
        return new ProductRequest(
                "Valid Name",
                "Valid description",
                new BigDecimal("99.99"),
                100,
                "A".repeat(51),
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }

    public static ProductRequest withTooLongDescription() {
        return new ProductRequest(
                "Valid Name",
                "A".repeat(5001),
                new BigDecimal("99.99"),
                100,
                "PROD-TEST-001",
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE);
    }
}
