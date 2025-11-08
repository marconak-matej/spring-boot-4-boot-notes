package io.github.mm.http.exchange.demo.rest.rest;

import io.github.mm.http.exchange.demo.rest.ProductDemoService;
import io.github.mm.http.exchange.product.api.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Product Demo", description = "Demonstrates HTTP Exchange feature with product operations")
public class ProductDemoApi {

    private final ProductDemoService service;

    public ProductDemoApi(ProductDemoService demoService) {
        this.service = demoService;
    }

    @Operation(
            summary = "Get all products",
            description = "Retrieves all products from the external API using @HttpExchange declarative HTTP client")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved all products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a specific product by its ID using @HttpExchange declarative HTTP client")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the product",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @GetMapping("/products/{id}")
    public Product getProductById(
            @Parameter(description = "Product ID", required = true, example = "ff80818193f68ac70193f68f38930000")
                    @PathVariable
                    String id) {
        return service.getProductById(id);
    }
}
