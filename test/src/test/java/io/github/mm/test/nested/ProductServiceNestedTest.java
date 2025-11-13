package io.github.mm.test.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.mm.test.product.ProductNotFoundException;
import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Product Service - Nested Tests with Enhanced DI (Spring 7)")
class ProductServiceNestedTest {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceNestedTest.class);

    @Autowired
    ProductService service;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        log.debug("Start of test {}", testInfo.getDisplayName());
        // Spring 7: DI works in lifecycle methods!

        service.clearAll();
    }

    @Nested
    @DisplayName("When product exists")
    class WhenProductExists {

        Product testProduct;

        @BeforeEach
        void createProduct(@Autowired ProductService service) {
            // Spring 7: Method parameters in @BeforeEach work!

            testProduct = service.createProduct("Laptop", 999.99);
        }

        @Test
        @DisplayName("Should find product by ID")
        void shouldFindById(@Autowired ProductService service) {
            // Spring 7: DI in test methods works!
            var found = service.getProductById(testProduct.id());

            assertThat(found).isNotNull().extracting(Product::name).isEqualTo("Laptop");

            assertThat(found.price()).isEqualTo(999.99);
        }

        @Test
        @DisplayName("Should return product with correct details")
        void shouldReturnCorrectDetails() {
            assertThat(testProduct).extracting(Product::name, Product::price).containsExactly("Laptop", 999.99);
        }

        @Nested
        @DisplayName("And product price needs updating")
        class AndPriceNeedsUpdating {

            @Test
            @DisplayName("Should update price successfully")
            void shouldUpdatePrice(@Autowired ProductService service) {
                // Spring 7: DI works at ANY nesting level!
                var updated = service.updatePrice(testProduct.id(), 899.99);

                assertThat(updated.price()).isEqualTo(899.99);
                assertThat(updated.name()).isEqualTo(testProduct.name());
            }

            @Test
            @DisplayName("Should persist price changes")
            void shouldPersistPriceChanges(@Autowired ProductService service) {
                // Update price
                service.updatePrice(testProduct.id(), 799.99);

                // Verify persisted
                var retrieved = service.getProductById(testProduct.id());
                assertThat(retrieved.price()).isEqualTo(799.99);
            }

            @Nested
            @DisplayName("And product needs to be deleted")
            class AndNeedsDeletion {

                @Test
                @DisplayName("Should delete product successfully")
                void shouldDeleteProduct(@Autowired ProductService service) {
                    // Spring 7: DI works even at 3rd nesting level!
                    service.deleteProduct(testProduct.id());

                    assertThatThrownBy(() -> service.getProductById(testProduct.id()))
                            .isInstanceOf(ProductNotFoundException.class)
                            .hasMessageContaining("Product not found");
                }
            }
        }

        @Nested
        @DisplayName("And checking product list")
        class AndCheckingList {

            @Test
            @DisplayName("Should appear in product list")
            void shouldAppearInList(@Autowired ProductService service) {
                var products = service.getAllProducts();

                assertThat(products).hasSize(1).extracting(Product::name).containsExactly("Laptop");
            }

            @Test
            @DisplayName("Should support multiple products")
            void shouldSupportMultipleProducts(@Autowired ProductService service) {
                // Add more products
                service.createProduct("Mouse", 29.99);
                service.createProduct("Keyboard", 79.99);

                var products = service.getAllProducts();
                assertThat(products).hasSize(3);
            }
        }
    }

    @Nested
    @DisplayName("When product does not exist")
    class WhenProductDoesNotExist {

        @Test
        @DisplayName("Should throw exception when finding by ID")
        void shouldThrowExceptionWhenFindingById(@Autowired ProductService service) {
            // Spring 7: DI in test methods works!
            assertThatThrownBy(() -> service.getProductById(999L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 999");
        }

        @Test
        @DisplayName("Should return empty list when no products")
        void shouldReturnEmptyListWhenNoProducts(@Autowired ProductService service) {
            var products = service.getAllProducts();

            assertThat(products).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when updating")
        void shouldThrowExceptionWhenUpdating(@Autowired ProductService service) {
            assertThatThrownBy(() -> service.updatePrice(999L, 100.0)).isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("When creating new products")
    class WhenCreatingProducts {

        @Test
        @DisplayName("Should create product with valid data")
        void shouldCreateWithValidData(@Autowired ProductService service) {
            var product = service.createProduct("Monitor", 299.99);

            assertThat(product).isNotNull();
            assertThat(product.id()).isNotNull();
            assertThat(product.name()).isEqualTo("Monitor");
            assertThat(product.price()).isEqualTo(299.99);
        }

        @Test
        @DisplayName("Should auto-generate ID")
        void shouldAutoGenerateId(@Autowired ProductService service) {
            var product1 = service.createProduct("Item1", 10.0);
            var product2 = service.createProduct("Item2", 20.0);

            assertThat(product1.id()).isNotNull();
            assertThat(product2.id()).isNotNull();
            assertThat(product1.id()).isNotEqualTo(product2.id());
        }

        @Nested
        @DisplayName("With validation")
        class WithValidation {

            @Test
            @DisplayName("Should enforce price validation")
            void shouldEnforcePriceValidation(@Autowired ProductService service) {
                assertThatThrownBy(() -> service.createProduct("Invalid", -100.0))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("Should enforce name validation")
            void shouldEnforceNameValidation(@Autowired ProductService service) {
                assertThatThrownBy(() -> service.createProduct("", 100.0)).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}
