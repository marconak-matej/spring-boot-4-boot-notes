package io.github.mm.test.product.internal;

import io.github.mm.test.product.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    void deleteById(Long id);

    void clear();
}
