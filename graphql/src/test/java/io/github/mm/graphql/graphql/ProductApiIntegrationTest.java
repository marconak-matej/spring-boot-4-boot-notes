package io.github.mm.graphql.graphql;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
@DisplayName("Product Controller Integration Tests")
class ProductApiIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    @DisplayName("Should complete full product lifecycle (create, read, update, delete)")
    void testCompleteProductLifecycle() {
        // Create a new product
        String productId = graphQlTester
                .document("""
                mutation {
                    createProduct(input: {
                        name: "Samsung Galaxy Z Fold"
                        data: {
                            color: "Phantom Black"
                            capacity: "512 GB"
                            generation: "3rd"
                        }
                    }) {
                        product {
                            id
                            name
                            data {
                                color
                                capacity
                                generation
                            }
                        }
                    }
                }
                """)
                .execute()
                .path("createProduct.product.name")
                .entity(String.class)
                .isEqualTo("Samsung Galaxy Z Fold")
                .path("createProduct.product.data.color")
                .entity(String.class)
                .isEqualTo("Phantom Black")
                .path("createProduct.product.data.capacity")
                .entity(String.class)
                .isEqualTo("512 GB")
                .path("createProduct.product.data.generation")
                .entity(String.class)
                .isEqualTo("3rd")
                .path("createProduct.product.id")
                .entity(String.class)
                .get();

        // Read the product
        graphQlTester
                .document("""
                query {
                    product(id: "%s") {
                        id
                        name
                        data {
                            color
                        }
                    }
                }
                """.formatted(productId))
                .execute()
                .path("product.name")
                .entity(String.class)
                .isEqualTo("Samsung Galaxy Z Fold")
                .path("product.data.color")
                .entity(String.class)
                .isEqualTo("Phantom Black");

        // Update the product
        graphQlTester
                .document("""
                mutation {
                    updateProduct(id: "%s", input: {
                        name: "Samsung Galaxy Z Fold 5"
                        data: {
                            color: "Icy Blue"
                            capacity: "1 TB"
                            generation: "5th"
                        }
                    }) {
                        product {
                            id
                            name
                            data {
                                color
                                capacity
                                generation
                            }
                        }
                    }
                }
                """.formatted(productId))
                .execute()
                .path("updateProduct.product.name")
                .entity(String.class)
                .isEqualTo("Samsung Galaxy Z Fold 5")
                .path("updateProduct.product.data.color")
                .entity(String.class)
                .isEqualTo("Icy Blue")
                .path("updateProduct.product.data.capacity")
                .entity(String.class)
                .isEqualTo("1 TB");

        // Verify the update persisted
        graphQlTester
                .document("""
                query {
                    product(id: "%s") {
                        name
                        data {
                            generation
                        }
                    }
                }
                """.formatted(productId))
                .execute()
                .path("product.name")
                .entity(String.class)
                .isEqualTo("Samsung Galaxy Z Fold 5")
                .path("product.data.generation")
                .entity(String.class)
                .isEqualTo("5th");

        // Delete the product
        graphQlTester
                .document("""
                mutation {
                    deleteProduct(id: "%s") {
                        success
                    }
                }
                """.formatted(productId))
                .execute()
                .path("deleteProduct.success")
                .entity(Boolean.class)
                .isEqualTo(true);

        // Verify deletion - should return an error
        graphQlTester.document("""
                query {
                    product(id: "%s") {
                        id
                    }
                }
                """.formatted(productId)).execute().errors().satisfy(errors -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.getFirst().getMessage()).contains("Product not found with id: " + productId);
        });
    }

    @Test
    @DisplayName("Should paginate correctly with large dataset")
    void testPaginationWithLargeDataset() {
        // Create additional products to test pagination
        for (var i = 1; i <= 15; i++) {
            graphQlTester.document("""
                    mutation {
                        createProduct(input: {
                            name: "Pagination Test Product %d"
                            data: {
                                color: "Color %d"
                                capacity: "256GB"
                                generation: "Gen %d"
                            }
                        }) {
                            product {
                                id
                            }
                        }
                    }
                    """.formatted(i, i, i)).execute();
        }

        // Test first page
        graphQlTester
                .document("""
                query {
                    products(page: 0, size: 5) {
                        content {
                            name
                        }
                        totalElements
                        totalPages
                        pageNumber
                        pageSize
                        hasNext
                        hasPrevious
                    }
                }
                """)
                .execute()
                .path("products.pageNumber")
                .entity(Integer.class)
                .isEqualTo(0)
                .path("products.pageSize")
                .entity(Integer.class)
                .isEqualTo(5)
                .path("products.hasNext")
                .entity(Boolean.class)
                .isEqualTo(true)
                .path("products.hasPrevious")
                .entity(Boolean.class)
                .isEqualTo(false)
                .path("products.content")
                .entityList(Object.class)
                .hasSize(5);

        // Test second page
        graphQlTester
                .document("""
                query {
                    products(page: 1, size: 5) {
                        pageNumber
                        hasNext
                        hasPrevious
                        content {
                            name
                        }
                    }
                }
                """)
                .execute()
                .path("products.pageNumber")
                .entity(Integer.class)
                .isEqualTo(1)
                .path("products.hasNext")
                .entity(Boolean.class)
                .isEqualTo(true)
                .path("products.hasPrevious")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Should update product partially while preserving unchanged fields")
    void testPartialUpdate() {
        // Create a product
        var productId = graphQlTester
                .document("""
                mutation {
                    createProduct(input: {
                        name: "Original Name"
                        data: {
                            color: "Original Color"
                            capacity: "128GB"
                            generation: "1st"
                        }
                    }) {
                        product {
                            id
                        }
                    }
                }
                """)
                .execute()
                .path("createProduct.product.id")
                .entity(String.class)
                .get();

        // Update only the name, keeping data unchanged
        graphQlTester
                .document("""
                mutation {
                    updateProduct(id: "%s", input: {
                        name: "Updated Name Only"
                    }) {
                        product {
                            id
                            name
                            data {
                                color
                                capacity
                                generation
                            }
                        }
                    }
                }
                """.formatted(productId))
                .execute()
                .path("updateProduct.product.name")
                .entity(String.class)
                .isEqualTo("Updated Name Only")
                .path("updateProduct.product.data.color")
                .entity(String.class)
                .isEqualTo("Original Color")
                .path("updateProduct.product.data.capacity")
                .entity(String.class)
                .isEqualTo("128GB");
    }

    @Test
    @DisplayName("Should return error when deleting non-existent product")
    void testDeleteNonExistentProduct() {
        // Try to delete a product that doesn't exist - should get an error
        graphQlTester.document("""
                mutation {
                    deleteProduct(id: "non-existent-id-999999") {
                        success
                    }
                }
                """).execute().errors().satisfy(errors -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.getFirst().getMessage()).contains("Product not found");
        });
    }

    @Test
    @DisplayName("Should return error when querying non-existent product")
    void testQueryNonExistentProduct() {
        // Query a product that doesn't exist - should get an error
        graphQlTester.document("""
                query {
                    product(id: "non-existent-id-999999") {
                        id
                        name
                    }
                }
                """).execute().errors().satisfy(errors -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.getFirst().getMessage()).contains("Product not found");
        });
    }
}
