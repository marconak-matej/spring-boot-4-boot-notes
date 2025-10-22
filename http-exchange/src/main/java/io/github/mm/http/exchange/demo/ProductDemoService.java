package io.github.mm.http.exchange.demo;

import io.github.mm.http.exchange.product.client.Product;
import io.github.mm.http.exchange.product.client.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductDemoService {

    private final ProductService service;

    public ProductDemoService(ProductService service) {
        this.service = service;
    }

    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    public Product getProductById(String id) {
        return service.getProductById(id);
    }
}
