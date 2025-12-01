package io.github.mm.jooq.product;

import static io.github.mm.jooq.product.fixtures.ProductRequestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.mm.jooq.infrastructure.exception.NotFoundException;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.test.SpringBootIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootIntegrationTest
@DisplayName("Product Service Integration Tests")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Should create product via service using jOOQ")
    void shouldCreateProduct() {
        var request = createMonitor();

        var product = productService.createProduct(request);

        assertThat(product).isNotNull();
        assertThat(product.id()).isNotNull();
        assertThat(product.name()).isEqualTo("Monitor");
        assertThat(product.createdBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("Should get product by ID via service using jOOQ")
    void shouldGetProductById() {
        var createdProduct = productService.createProduct(createMonitor());

        var product = productService.getProductById(createdProduct.id());

        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo(createdProduct.id());
        assertThat(product.name()).isEqualTo("Monitor");
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent product via service using jOOQ")
    void shouldThrowExceptionWhenGettingNonExistentProduct() {
        assertThatThrownBy(() -> productService.getProductById(99999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should get all products via service using jOOQ")
    void shouldFindAll() {
        productService.createProduct(createMonitor());
        productService.createProduct(createWithStatus(ProductStatus.INACTIVE));

        var products = productService.findAll(Pageable.ofSize(100), null, null);

        assertThat(products).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should filter products by status via service using jOOQ")
    void shouldFilterProductsByStatus() {
        productService.createProduct(createWithStatus(ProductStatus.ACTIVE));
        productService.createProduct(createWithStatus(ProductStatus.INACTIVE));

        var activeProducts = productService.findAll(Pageable.ofSize(100), ProductStatus.ACTIVE, null);

        assertThat(activeProducts).isNotEmpty().allMatch(product -> ProductStatus.ACTIVE.equals(product.status()));
    }

    @Test
    @DisplayName("Should filter products by category via service using jOOQ")
    void shouldFilterProductsByCategory() {
        productService.createProduct(createMonitor());

        var electronicsProducts = productService.findAll(Pageable.ofSize(100), null, "ELECTRONICS");

        assertThat(electronicsProducts).isNotEmpty().allMatch(product -> "ELECTRONICS"
                .equalsIgnoreCase(product.category()));
    }

    @Test
    @DisplayName("Should update product via service using jOOQ")
    void shouldUpdateProduct() {
        var createdProduct = productService.createProduct(createMonitor());

        var updatedProduct = productService.updateProduct(createdProduct.id(), updateRequest());

        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.id()).isEqualTo(createdProduct.id());
        assertThat(updatedProduct.name()).isEqualTo("Updated Product");
        assertThat(updatedProduct.updatedBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product via service using jOOQ")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        assertThatThrownBy(() -> productService.updateProduct(99999L, updateRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99999");
    }

    @Test
    @DisplayName("Should delete product via service using jOOQ")
    void shouldDeleteProduct() {
        var createdProduct = productService.createProduct(createMonitor());

        productService.deleteProduct(createdProduct.id());

        assertThatThrownBy(() -> productService.getProductById(createdProduct.id()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product via service using jOOQ")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        assertThatThrownBy(() -> productService.deleteProduct(99999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99999");
    }
}
