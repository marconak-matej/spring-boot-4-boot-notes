package io.github.mm.http.exchange.product.client;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductService {

    @GetExchange("/objects")
    List<Product> getAllProducts();

    @GetExchange("/objects/{id}")
    Product getProductById(@PathVariable String id);
}
