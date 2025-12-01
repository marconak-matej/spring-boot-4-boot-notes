package io.github.mm.jooq.product.repository;

import static io.github.mm.jooq.product.fixtures.ProductRequestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.jooq.product.generated.tables.records.ProductsRecord;
import io.github.mm.jooq.product.rest.dto.CreateProductRequest;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.test.SpringBootIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootIntegrationTest
@DisplayName("Product Repository Integration Tests")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("Should create and retrieve product using jOOQ")
    void shouldCreateAndRetrieveProduct() {
        var request = createMonitor();

        var savedProduct = repository.create(request, "test-user");

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Monitor");
        assertThat(savedProduct.getDescription()).isEqualTo("4K UHD Monitor");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("599.99"));
        assertThat(savedProduct.getStockQuantity()).isEqualTo(50);
        assertThat(savedProduct.getSku()).isEqualTo("PROD-MONITOR-001");
        assertThat(savedProduct.getCategory()).isEqualTo("ELECTRONICS");
        assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVE.name());
        assertThat(savedProduct.getCreatedAt()).isNotNull();
        assertThat(savedProduct.getCreatedBy()).isEqualTo("test-user");

        var retrievedProduct = repository.findById(savedProduct.getId());
        assertThat(retrievedProduct)
                .isPresent()
                .get()
                .extracting(ProductsRecord::getName)
                .isEqualTo("Monitor");
    }

    @Test
    @DisplayName("Should find products by status using jOOQ")
    void shouldFindProductsByStatus() {
        repository.create(createWithStatus(ProductStatus.ACTIVE), "test-user");
        repository.create(createWithStatus(ProductStatus.INACTIVE), "test-user");

        var activeProducts = repository.findAll(Pageable.ofSize(100), ProductStatus.ACTIVE, null);

        assertThat(activeProducts)
                .isNotEmpty()
                .allMatch(product -> ProductStatus.ACTIVE.name().equals(product.getStatus()));
    }

    @Test
    @DisplayName("Should find products by category using jOOQ")
    void shouldFindProductsByCategory() {
        repository.create(createMonitor(), "test-user");

        var electronicsProducts = repository.findAll(Pageable.ofSize(100), null, "ELECTRONICS");

        assertThat(electronicsProducts).isNotEmpty().allMatch(product -> "ELECTRONICS"
                .equalsIgnoreCase(product.getCategory()));
    }

    @Test
    @DisplayName("Should find products by status and category using jOOQ")
    void shouldFindProductsByStatusAndCategory() {
        repository.create(createMonitor(), "test-user");
        repository.create(createWithStatus(ProductStatus.INACTIVE), "test-user");

        var filteredProducts = repository.findAll(Pageable.ofSize(100), ProductStatus.ACTIVE, "ELECTRONICS");

        assertThat(filteredProducts)
                .isNotEmpty()
                .allMatch(product -> ProductStatus.ACTIVE.name().equals(product.getStatus())
                        && "ELECTRONICS".equalsIgnoreCase(product.getCategory()));
    }

    @Test
    @DisplayName("Should update product using jOOQ")
    void shouldUpdateProduct() {
        var savedProduct = repository.create(createMonitor(), "test-user");
        var productId = savedProduct.getId();

        var updateRequest = updateRequest();
        var updatedProduct = repository.update(productId, updateRequest, "updater-user");

        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getId()).isEqualTo(productId);
        assertThat(updatedProduct.get().getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.get().getDescription()).isEqualTo("Updated description");
        assertThat(updatedProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(updatedProduct.get().getUpdatedAt()).isNotNull();
        assertThat(updatedProduct.get().getUpdatedBy()).isEqualTo("updater-user");
    }

    @Test
    @DisplayName("Should return empty when updating non-existent product using jOOQ")
    void shouldReturnEmptyWhenUpdatingNonExistentProduct() {
        var result = repository.update(99999L, updateRequest(), "test-user");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete product using jOOQ")
    void shouldDeleteProduct() {
        var savedProduct = repository.create(createMonitor(), "test-user");
        var productId = savedProduct.getId();

        boolean deleted = repository.deleteById(productId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(productId)).isEmpty();
    }

    @Test
    @DisplayName("Should return false when deleting non-existent product using jOOQ")
    void shouldReturnFalseWhenDeletingNonExistentProduct() {
        boolean deleted = repository.deleteById(99999L);

        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("Should handle minimal product creation using jOOQ")
    void shouldHandleMinimalProductCreation() {
        var request = createMinimal();

        var savedProduct = repository.create(request, "test-user");

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Minimal Product");
        assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("Should set default status to ACTIVE when not provided using jOOQ")
    void shouldSetDefaultStatusToActive() {
        var request = new CreateProductRequest(
                "No Status Product", "Description", new BigDecimal("50.00"), 10, "PROD-NO-STATUS", "TEST", null);

        var savedProduct = repository.create(request, "test-user");

        assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("Should retrieve all products using jOOQ")
    void shouldRetrieveAllProducts() {
        repository.create(createMonitor(), "test-user");
        repository.create(createWithStatus(ProductStatus.INACTIVE), "test-user");

        var allProducts = repository.findAll(Pageable.ofSize(100), null, null);

        assertThat(allProducts).hasSizeGreaterThanOrEqualTo(2);
    }
}
