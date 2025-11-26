package io.github.mm.flyway.product.internal;

import static io.github.mm.flyway.product.fixtures.ProductFixtures.*;
import static io.github.mm.flyway.product.fixtures.ProductRequestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product Mapper Tests")
class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    @DisplayName("Should convert Product entity to ProductResponse DTO")
    void shouldConvertEntityToResponse() {
        var product = withId(laptop(), 1L);

        var response = mapper.toResponse(product);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Laptop");
        assertThat(response.description()).isEqualTo("High-performance laptop");
        assertThat(response.category()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(response.status()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return null when converting null entity to response")
    void shouldReturnNullWhenEntityIsNull() {
        var response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should convert ProductRequest DTO to Product entity for creation")
    void shouldConvertRequestToEntityForCreation() {
        var request = reqMonitor();

        var product = mapper.toEntity(request);

        assertThat(product).isNotNull();
        assertThat(product.id()).isNull();
        assertThat(product.name()).isEqualTo("Monitor");
        assertThat(product.description()).isEqualTo("4K UHD Monitor");
        assertThat(product.category()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.createdAt()).isNull();
        assertThat(product.createdBy()).isNull();
    }

    @Test
    @DisplayName("Should set default status when not provided in request")
    void shouldSetDefaultStatusWhenNotProvided() {
        var request = minimal();

        var product = mapper.toEntity(request);

        assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should convert ProductRequest DTO to Product entity for update")
    void shouldConvertRequestToEntityForUpdate() {
        var request = withStatus(ProductStatus.INACTIVE);

        var product = mapper.toEntity(5L, request);

        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo(5L);
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.status()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.updatedAt()).isNull();
        assertThat(product.updatedBy()).isNull();
    }

    @Test
    @DisplayName("Should return null when converting null request to entity")
    void shouldReturnNullWhenRequestIsNull() {
        var product = mapper.toEntity(null);
        assertThat(product).isNull();

        var productWithId = mapper.toEntity(1L, null);
        assertThat(productWithId).isNull();
    }
}
