package io.github.mm.jooq.product.rest;

import static io.github.mm.jooq.product.fixtures.ProductJsonFixtures.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import io.github.mm.jooq.test.SpringBootIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootIntegrationTest
@DisplayName("Product Controller Integration Tests")
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should create new product via REST API using jOOQ")
    void shouldCreateNewProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webcam()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", equalTo("Webcam")))
                .andExpect(jsonPath("$.description", equalTo("HD Webcam with microphone")))
                .andExpect(jsonPath("$.price", equalTo(89.99)))
                .andExpect(jsonPath("$.stockQuantity", equalTo(30)))
                .andExpect(jsonPath("$.sku", equalTo("PROD-WEBCAM-001")))
                .andExpect(jsonPath("$.category", equalTo("ELECTRONICS")))
                .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.createdBy", notNullValue()));
    }

    @Test
    @DisplayName("Should get product by ID via REST API using jOOQ")
    void shouldGetProductById() throws Exception {
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webcam()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer productId = JsonPath.parse(createResponse).read("$.id");

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(productId)))
                .andExpect(jsonPath("$.name", equalTo("Webcam")));
    }

    @Test
    @DisplayName("Should update existing product via REST API using jOOQ")
    void shouldUpdateExistingProduct() throws Exception {
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webcam()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer productId = JsonPath.parse(createResponse).read("$.id");

        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(productId)))
                .andExpect(jsonPath("$.name", equalTo("Updated Product")))
                .andExpect(jsonPath("$.description", equalTo("Updated description")))
                .andExpect(jsonPath("$.price", equalTo(149.99)))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.updatedBy", notNullValue()));
    }

    @Test
    @DisplayName("Should delete product via REST API using jOOQ")
    void shouldDeleteProduct() throws Exception {
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRequest()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer productId = JsonPath.parse(createResponse).read("$.id");

        mockMvc.perform(delete("/api/products/{id}", productId)).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", productId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all products via REST API using jOOQ")
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webcam()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Should filter products by status via REST API using jOOQ")
    void shouldFilterProductsByStatus() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createElectronics()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.content[*].status", everyItem(equalTo("ACTIVE"))));
    }

    @Test
    @DisplayName("Should filter products by category via REST API using jOOQ")
    void shouldFilterProductsByCategory() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createElectronics()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products").param("category", "ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Should filter products by status and category via REST API using jOOQ")
    void shouldFilterProductsByStatusAndCategory() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createElectronics()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products").param("status", "ACTIVE").param("category", "ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Should return 404 for non-existent product via REST API using jOOQ")
    void shouldReturn404ForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product via REST API using jOOQ")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        mockMvc.perform(put("/api/products/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent product via REST API using jOOQ")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 99999)).andExpect(status().isNotFound());
    }
}
