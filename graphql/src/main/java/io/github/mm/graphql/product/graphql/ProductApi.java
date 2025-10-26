package io.github.mm.graphql.product.graphql;

import io.github.mm.graphql.product.ProductService;
import io.github.mm.graphql.product.graphql.model.types.*;
import io.github.mm.graphql.product.internal.ProductMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductApi {

    private final ProductService service;
    private final ProductMapper mapper;

    public ProductApi(ProductService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @QueryMapping
    public Product product(@Argument String id) {
        var product = service.getProductById(id);
        return mapper.toGraphQLProduct(product);
    }

    @QueryMapping
    public ProductPage products(@Argument int page, @Argument int size) {
        var products = service.getProducts(page, size);
        return mapper.toGraphQLProductPage(products);
    }

    @MutationMapping
    public CreateProductPayload createProduct(@Argument CreateProductInput input) {
        var req = mapper.toCreateProductDto(input);
        var product = service.createProduct(req);
        return mapper.toCreateProductPayload(product);
    }

    @MutationMapping
    public UpdateProductPayload updateProduct(@Argument String id, @Argument UpdateProductInput input) {
        var req = mapper.toUpdateProductDto(input);
        var product = service.updateProduct(id, req);
        return mapper.toUpdateProductPayload(product);
    }

    @MutationMapping
    public DeleteProductPayload deleteProduct(@Argument String id) {
        var deleted = service.deleteProduct(id);
        return mapper.toDeleteProductPayload(deleted);
    }
}
