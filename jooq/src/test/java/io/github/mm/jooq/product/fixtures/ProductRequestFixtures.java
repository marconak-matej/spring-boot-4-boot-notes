package io.github.mm.jooq.product.fixtures;

import io.github.mm.jooq.product.rest.dto.CreateProductRequest;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.product.rest.dto.UpdateProductRequest;
import java.math.BigDecimal;

public final class ProductRequestFixtures {

    private ProductRequestFixtures() {}

    public static CreateProductRequest createMonitor() {
        return new CreateProductRequest(
                "Monitor",
                "4K UHD Monitor",
                new BigDecimal("599.99"),
                50,
                "PROD-MONITOR-001",
                "ELECTRONICS",
                ProductStatus.ACTIVE);
    }

    public static CreateProductRequest createWithStatus(ProductStatus status) {
        return new CreateProductRequest(
                "Test Product",
                "Test description",
                new BigDecimal("99.99"),
                100,
                "PROD-TEST-001",
                "ELECTRONICS",
                status);
    }

    public static CreateProductRequest createMinimal() {
        return new CreateProductRequest(
                "Minimal Product", null, new BigDecimal("9.99"), 7, "PROD-UPDATED-001", null, ProductStatus.ACTIVE);
    }

    public static UpdateProductRequest updateRequest() {
        return new UpdateProductRequest(
                "Updated Product",
                "Updated description",
                new BigDecimal("149.99"),
                75,
                "PROD-UPDATED-001",
                "ELECTRONICS",
                ProductStatus.ACTIVE);
    }

    public static UpdateProductRequest updateWithStatus(ProductStatus status) {
        return new UpdateProductRequest(
                "Updated Status Product",
                "Product with updated status",
                new BigDecimal("199.99"),
                50,
                "PROD-STATUS-001",
                "ELECTRONICS",
                status);
    }
}
