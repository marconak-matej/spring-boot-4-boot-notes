package io.github.mm.jooq.product;

import io.github.mm.jooq.infrastructure.exception.NotFoundException;
import io.github.mm.jooq.product.internal.ProductMapper;
import io.github.mm.jooq.product.repository.ProductRepository;
import io.github.mm.jooq.product.rest.dto.CreateProductRequest;
import io.github.mm.jooq.product.rest.dto.Product;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.product.rest.dto.UpdateProductRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        return mapper.toProduct(repository.create(request, "system"));
    }

    public Product getProductById(Long id) {
        return repository
                .findById(id)
                .map(mapper::toProduct)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    public Page<@NonNull Product> findAll(Pageable pageable, ProductStatus status, String category) {
        return repository.findAll(pageable, status, category).map(mapper::toProduct);
    }

    @Transactional
    public Product updateProduct(Long id, UpdateProductRequest request) {
        return repository
                .update(id, request, "system")
                .map(mapper::toProduct)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!repository.deleteById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
    }
}
