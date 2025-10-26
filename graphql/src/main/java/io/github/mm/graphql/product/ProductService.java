package io.github.mm.graphql.product;

import io.github.mm.graphql.infrastructure.exception.NotFoundException;
import io.github.mm.graphql.product.model.CreateProduct;
import io.github.mm.graphql.product.model.Product;
import io.github.mm.graphql.product.model.UpdateProduct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final Map<String, Product> store = new ConcurrentHashMap<>();

    public Product getProductById(String id) {
        return store.computeIfAbsent(id, v -> {
            throw new NotFoundException("Product not found with id: " + id);
        });
    }

    public Page<@NonNull Product> getProducts(int page, int size) {
        var allProducts = List.copyOf(store.values());
        var totalElements = allProducts.size();

        var fromIndex = page * size;
        var toIndex = Math.min(fromIndex + size, totalElements);

        var content = (fromIndex < totalElements) ? allProducts.subList(fromIndex, toIndex) : List.<Product>of();

        return new PageImpl<>(content, PageRequest.of(page, size), allProducts.size());
    }

    public Product createProduct(CreateProduct input) {
        validateName(input.name());
        var id = UUID.randomUUID().toString();
        var product = new Product(id, input.name(), input.data());
        store.put(id, product);
        return product;
    }

    public Product updateProduct(String id, UpdateProduct input) {
        return store.compute(id, (k, existingProduct) -> {
            if (existingProduct == null) {
                throw new NotFoundException("Product not found with id: " + id);
            }

            var newName = existingProduct.name();
            var newData = existingProduct.data();

            if (input.name() != null) {
                validateName(input.name());
                newName = input.name();
            }
            if (input.data() != null) {
                newData = input.data();
            }

            return new Product(id, newName, newData);
        });
    }

    public boolean deleteProduct(String id) {
        return Optional.ofNullable(store.remove(id))
                .map(v -> true)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
    }
}
