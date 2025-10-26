package io.github.mm.graphql.graphql;

import io.github.mm.graphql.product.ProductService;
import io.github.mm.graphql.product.graphql.ProductApi;
import io.github.mm.graphql.product.internal.ProductMapper;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(ProductApi.class)
@Import({ProductService.class, ProductMapper.class})
@DisplayName("Product Controller Unit Tests")
class ProductApiTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    @DisplayName("Should create a product and retrieve it by ID")
    void testCreateAndGetProduct() {
        // First create a product
        var createResponse = graphQlTester
                .document("""
                mutation {
                    createProduct(input: {
                        name: "Test Product"
                        data: {
                            color: "Black"
                            capacity: "128GB"
                            generation: "1st"
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
                .isEqualTo("Test Product")
                .path("createProduct.product.data.color")
                .entity(String.class)
                .isEqualTo("Black")
                .path("createProduct.product.id")
                .entity(String.class)
                .get();

        // Then query it by ID
        graphQlTester
                .document("""
                query {
                    product(id: "%s") {
                        id
                        name
                        data {
                            color
                            capacity
                            generation
                        }
                    }
                }
                """.formatted(createResponse))
                .execute()
                .path("product.id")
                .entity(String.class)
                .isEqualTo(createResponse)
                .path("product.name")
                .entity(String.class)
                .isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should update an existing product")
    void testUpdateProduct() {
        // Create a product
        var productId = graphQlTester
                .document("""
                mutation {
                    createProduct(input: {
                        name: "Original Product"
                        data: {
                            color: "White"
                            capacity: "64GB"
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

        // Update it
        graphQlTester
                .document("""
                mutation {
                    updateProduct(id: "%s", input: {
                        name: "Updated Product"
                        data: {
                            color: "Black"
                            capacity: "256GB"
                            generation: "2nd"
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
                .isEqualTo("Updated Product")
                .path("updateProduct.product.data.color")
                .entity(String.class)
                .isEqualTo("Black");
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void testDeleteProduct() {
        // Create a product
        var productId = graphQlTester
                .document("""
                mutation {
                    createProduct(input: {
                        name: "To Be Deleted"
                        data: {
                            color: "Red"
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

        // Delete it
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

        // Verify it's deleted - should return an error
        graphQlTester.document("""
                query {
                    product(id: "%s") {
                        id
                    }
                }
                """.formatted(productId)).execute().errors().expect(error -> Objects.requireNonNull(
                        error.getMessage())
                .contains("Product not found with id: " + productId));
    }

    @Test
    @DisplayName("Should paginate products correctly")
    void testProductsPagination() {
        // Create multiple products
        for (var i = 1; i <= 5; i++) {
            graphQlTester.document("""
                    mutation {
                        createProduct(input: {
                            name: "Product %d"
                            data: {
                                color: "Color %d"
                                capacity: "128GB"
                                generation: "1st"
                            }
                        }) {
                            product {
                                id
                            }
                        }
                    }
                    """.formatted(i, i)).execute();
        }

        // Test pagination
        graphQlTester
                .document("""
                query {
                    products(page: 0, size: 2) {
                        content {
                            id
                            name
                        }
                        totalElements
                        pageSize
                        pageNumber
                        hasNext
                    }
                }
                """)
                .execute()
                .path("products.pageSize")
                .entity(Integer.class)
                .isEqualTo(2)
                .path("products.pageNumber")
                .entity(Integer.class)
                .isEqualTo(0)
                .path("products.hasNext")
                .entity(Boolean.class)
                .isEqualTo(true);
    }
}
