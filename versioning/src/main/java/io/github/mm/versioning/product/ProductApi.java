package io.github.mm.versioning.product;

import io.github.mm.versioning.product.domain.Product;
import io.github.mm.versioning.product.domain.ProductV2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.mm.versioning.product.domain.Currency.USD;
import static io.github.mm.versioning.product.domain.ProductV2Status.IN_STOCK;

@RestController
@RequestMapping("/api/products")
public class ProductApi {

    @GetMapping(path = "/{id}", version = "1.0")
    public Product getProductV1(@PathVariable
                                @NotBlank(message = "Product ID cannot be blank")
                                @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Product ID must be alphanumeric")
                                String id) {
        return new Product(id, "Coffee Maker", 49.99, true);
    }

    @GetMapping(path = "/{id}", version = "2.0")
    public ProductV2 getProductV2(@PathVariable
                                  @NotBlank(message = "Product ID cannot be blank")
                                  @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Product ID must be alphanumeric")
                                  String id) {
        return new ProductV2(id, "Coffee Maker", 4999, USD, IN_STOCK);
    }
}
