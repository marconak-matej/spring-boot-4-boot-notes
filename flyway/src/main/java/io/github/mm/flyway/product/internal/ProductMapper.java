package io.github.mm.flyway.product.internal;

import io.github.mm.flyway.product.domain.Product;
import io.github.mm.flyway.product.domain.ProductStatus;
import io.github.mm.flyway.product.rest.dto.ProductRequest;
import io.github.mm.flyway.product.rest.dto.ProductResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public PagedModel<@NonNull ProductResponse> toPageModel(Pageable pageable, Page<@NonNull Product> page) {
        var items = page.stream().map(this::toResponse).toList();

        return new PagedModel<>(PageableExecutionUtils.getPage(items, pageable, page::getTotalElements));
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity(),
                product.sku(),
                product.category(),
                product.status(),
                product.createdAt(),
                product.updatedAt());
    }

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        return new Product(
                null,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.sku(),
                request.category(),
                mapStatus(request),
                null,
                null,
                null,
                null);
    }

    public Product toEntity(Long id, ProductRequest request) {
        if (request == null) {
            return null;
        }
        return new Product(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.sku(),
                request.category(),
                mapStatus(request),
                null,
                null,
                null,
                null);
    }

    private static ProductStatus mapStatus(ProductRequest request) {
        return request.status() != null ? request.status() : ProductStatus.ACTIVE;
    }
}
