package io.github.mm.test.product.rest;

import io.github.mm.test.product.ProductNotFoundException;
import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductApi {

    private final ProductService service;

    public ProductApi(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody CreateProductRequest request) {
        return service.createProduct(request.name(), request.price());
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @PutMapping("/{id}")
    public Product updatePrice(@PathVariable Long id, @RequestBody UpdatePriceRequest request) {
        return service.updatePrice(id, request.price());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    public record CreateProductRequest(String name, Double price) {}

    public record UpdatePriceRequest(Double price) {}

    public record ErrorResponse(String message) {}
}
