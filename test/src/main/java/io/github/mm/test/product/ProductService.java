package io.github.mm.test.product;

import io.github.mm.test.product.internal.ProductRepository;
import io.github.mm.test.product.model.Product;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(String name, Double price) {
        var product = new Product(null, name, price);
        return repository.save(product);
    }

    public Product getProductById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product updatePrice(Long id, Double newPrice) {
        var product = getProductById(id);
        var updated = new Product(product.id(), product.name(), newPrice);
        return repository.save(updated);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    public void clearAll() {
        repository.clear();
    }
}
