package io.github.mm.http.exchange.demo.rest.rest;

import io.github.mm.http.exchange.demo.rest.ProductDemoService;
import io.github.mm.http.exchange.product.api.Product;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class ProductDemoApi {

    private final ProductDemoService service;

    public ProductDemoApi(ProductDemoService demoService) {
        this.service = demoService;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable String id) {
        return service.getProductById(id);
    }
}
