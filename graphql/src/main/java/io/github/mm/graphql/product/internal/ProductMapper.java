package io.github.mm.graphql.product.internal;

import io.github.mm.graphql.product.graphql.model.types.*;
import io.github.mm.graphql.product.model.CreateProduct;
import io.github.mm.graphql.product.model.UpdateProduct;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // ========== GraphQL Input -> DTO conversions ==========

    public CreateProduct toCreateProductDto(CreateProductInput input) {
        if (input == null) {
            return null;
        }
        return new CreateProduct(input.getName(), toModelProductData(input.getData()));
    }

    public UpdateProduct toUpdateProductDto(UpdateProductInput input) {
        if (input == null) {
            return null;
        }
        return new UpdateProduct(input.getName(), toModelProductData(input.getData()));
    }

    public io.github.mm.graphql.product.model.ProductData toModelProductData(ProductDataInput input) {
        if (input == null) {
            return null;
        }
        return new io.github.mm.graphql.product.model.ProductData(
                input.getColor(), input.getCapacity(), input.getGeneration());
    }

    // ========== Model -> GraphQL Output conversions ==========

    public Product toGraphQLProduct(io.github.mm.graphql.product.model.Product model) {
        if (model == null) {
            return null;
        }
        return Product.newBuilder()
                .id(model.id())
                .name(model.name())
                .data(toGraphQLProductData(model.data()))
                .build();
    }

    public ProductData toGraphQLProductData(io.github.mm.graphql.product.model.ProductData model) {
        if (model == null) {
            return null;
        }
        return ProductData.newBuilder()
                .color(model.color())
                .capacity(model.capacity())
                .generation(model.generation())
                .build();
    }

    public ProductPage toGraphQLProductPage(
            @SuppressWarnings("NullableProblems") Page<io.github.mm.graphql.product.model.Product> page) {
        if (page == null) {
            return null;
        }
        return ProductPage.newBuilder()
                .content(page.stream().map(this::toGraphQLProduct).toList())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getPageable().getPageNumber())
                .pageSize(page.getPageable().getPageSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public CreateProductPayload toCreateProductPayload(io.github.mm.graphql.product.model.Product model) {
        if (model == null) {
            return null;
        }
        return CreateProductPayload.newBuilder()
                .product(toGraphQLProduct(model))
                .build();
    }

    public UpdateProductPayload toUpdateProductPayload(io.github.mm.graphql.product.model.Product model) {
        if (model == null) {
            return null;
        }
        return UpdateProductPayload.newBuilder()
                .product(toGraphQLProduct(model))
                .build();
    }

    public DeleteProductPayload toDeleteProductPayload(boolean success) {
        return DeleteProductPayload.newBuilder().success(success).build();
    }
}
