package io.github.mm.flyway.product.rest;

import static io.github.mm.flyway.product.fixtures.ProductJsonFixtures.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import io.github.mm.flyway.test.SpringBootIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootIntegrationTest
@DisplayName("Product Api Integration Tests")
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should create new product with request DTO")
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
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Should update existing product with request DTO")
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
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        mockMvc.perform(delete("/api/products/{id}", productId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should delete product")
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
    @DisplayName("Should return 400 for invalid product request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 for non-existent product")
    void shouldReturn404ForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999)).andExpect(status().isNotFound());
    }
}
