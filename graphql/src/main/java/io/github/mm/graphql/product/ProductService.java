package io.github.mm.graphql.product;

import io.github.mm.graphql.infrastructure.exception.NotFoundException;
import io.github.mm.graphql.product.model.CreateProduct;
import io.github.mm.graphql.product.model.Product;
import io.github.mm.graphql.product.model.UpdateProduct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final Map<String, Product> store = new ConcurrentHashMap<>();

    public Product getProductById(String id) {
        return store.computeIfAbsent(id, _ -> {
            LOGGER.atError()
                    .setMessage("Product not found")
                    .addKeyValue("productId", id)
                    .addKeyValue("operation", "getProductById")
                    .log();
            throw new NotFoundException("Product not found with id: " + id);
        });
    }

    public Page<@NonNull Product> getProducts(int page, int size) {
        var allProducts = List.copyOf(store.values());
        var totalElements = allProducts.size();

        var fromIndex = page * size;
        var toIndex = Math.min(fromIndex + size, totalElements);

        var content = (fromIndex < totalElements) ? allProducts.subList(fromIndex, toIndex) : List.<Product>of();

        var result = new PageImpl<>(content, PageRequest.of(page, size), allProducts.size());

        LOGGER.atInfo()
                .setMessage("Products retrieved for page {}")
                .addArgument(page)
                .addKeyValue("operation", "getProducts")
                .addKeyValue("page", page)
                .addKeyValue("size", size)
                .addKeyValue("totalElements", totalElements)
                .addKeyValue("returnedCount", content.size())
                .log();

        return result;
    }

    public Product createProduct(CreateProduct input) {
        validateName(input.name());
        var id = UUID.randomUUID().toString();
        var product = new Product(id, input.name(), input.data());
        store.put(id, product);

        LOGGER.atInfo()
                .setMessage("Product created")
                .addKeyValue("productId", id)
                .addKeyValue("operation", "createProduct")
                .addKeyValue("productName", input.name())
                .log();

        return product;
    }

    public Product updateProduct(String id, UpdateProduct input) {
        return store.compute(id, (_, existingProduct) -> {
            if (existingProduct == null) {
                LOGGER.atError()
                        .setMessage("Product not found for update")
                        .addKeyValue("productId", id)
                        .addKeyValue("operation", "updateProduct")
                        .log();
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

            var updated = new Product(id, newName, newData);

            LOGGER.atInfo()
                    .setMessage("Product updated")
                    .addKeyValue("productId", id)
                    .addKeyValue("operation", "updateProduct")
                    .addKeyValue("nameUpdated", !newName.equals(existingProduct.name()))
                    .addKeyValue("dataUpdated", !newData.equals(existingProduct.data()))
                    .log();

            return updated;
        });
    }

    public boolean deleteProduct(String id) {
        return Optional.ofNullable(store.remove(id))
                .map(_ -> {
                    LOGGER.atInfo()
                            .setMessage("Product deleted")
                            .addKeyValue("productId", id)
                            .addKeyValue("operation", "deleteProduct")
                            .log();
                    return true;
                })
                .orElseThrow(() -> {
                    LOGGER.atError()
                            .setMessage("Product not found for deletion")
                            .addKeyValue("productId", id)
                            .addKeyValue("operation", "deleteProduct")
                            .log();
                    return new NotFoundException("Product not found with id: " + id);
                });
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
    }
}
