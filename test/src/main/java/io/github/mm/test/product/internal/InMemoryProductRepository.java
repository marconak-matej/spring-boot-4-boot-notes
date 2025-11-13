package io.github.mm.test.product.internal;

import io.github.mm.test.product.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final ConcurrentHashMap<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        var productToSave = product;
        if (product.id() == null) {
            // Generate new ID if not provided
            productToSave = new Product(idGenerator.getAndIncrement(), product.name(), product.price());
        }
        store.put(productToSave.id(), productToSave);
        return productToSave;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}
