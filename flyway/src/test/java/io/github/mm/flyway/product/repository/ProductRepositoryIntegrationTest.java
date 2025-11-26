package io.github.mm.flyway.product.repository;

import static io.github.mm.flyway.product.fixtures.ProductFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.flyway.product.domain.Product;
import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import io.github.mm.flyway.test.SpringBootIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@SpringBootIntegrationTest
@DisplayName("Product Repository Integration Tests")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("Should find product by status")
    void shouldFindProductByStatus() {
        repository.save(laptop());
        repository.save(inactiveProduct());

        var activeProducts = repository.findByStatus(Pageable.ofSize(10), ProductStatus.ACTIVE);

        assertThat(activeProducts).isNotEmpty().allMatch(product -> ProductStatus.ACTIVE.equals(product.status()));
    }

    @Test
    @DisplayName("Should find products by category")
    void shouldFindProductsByCategory() {
        repository.save(laptop());
        repository.save(mouse());

        var electronicsProducts = repository.findByCategory(Pageable.ofSize(10), ProductCategory.ELECTRONICS);

        assertThat(electronicsProducts)
                .isNotEmpty()
                .allMatch(product -> ProductCategory.ELECTRONICS.equals(product.category()));
    }

    @Test
    @DisplayName("Should save and retrieve product")
    void shouldSaveAndRetrieveProduct() {
        var savedProduct = repository.save(monitor());

        assertThat(savedProduct.id()).isNotNull();
        assertThat(savedProduct.name()).isEqualTo("4K Monitor");

        var retrievedProduct = repository.findById(savedProduct.id());
        assertThat(retrievedProduct).isPresent().get().extracting(Product::name).isEqualTo("4K Monitor");
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        var savedProduct = repository.save(webcam());
        var productId = savedProduct.id();

        repository.deleteById(productId);

        var deletedProduct = repository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }
}
