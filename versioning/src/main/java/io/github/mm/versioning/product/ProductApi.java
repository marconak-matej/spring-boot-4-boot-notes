package io.github.mm.versioning.product;

import static io.github.mm.versioning.product.domain.Currency.USD;
import static io.github.mm.versioning.product.domain.ProductV2Status.IN_STOCK;

import io.github.mm.versioning.product.domain.Product;
import io.github.mm.versioning.product.domain.ProductV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product API with version-based routing (Spring Boot 4.0 feature)")
public class ProductApi {

    @Operation(
            summary = "Get product by ID (v1.0)",
            description =
                    "Returns product information in v1.0 format with decimal price and boolean availability. Use Accept-Version: 1.0 header.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved product in v1.0 format",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(responseCode = "400", description = "Invalid product ID format", content = @Content),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
            })
    @GetMapping(path = "/{id}", version = "1.0")
    public Product getProductV1(
            @Parameter(description = "Alphanumeric product identifier", required = true, example = "prod-123")
                    @PathVariable
                    @NotBlank(message = "Product ID cannot be blank")
                    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Product ID must be alphanumeric")
                    String id) {
        return new Product(id, "Coffee Maker", 49.99, true);
    }

    @Operation(
            summary = "Get product by ID (v2.0)",
            description =
                    "Returns product information in v2.0 format with integer price in cents, currency, and status enum. Use Accept-Version: 2.0 header.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved product in v2.0 format",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductV2.class))),
                @ApiResponse(responseCode = "400", description = "Invalid product ID format", content = @Content),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
            })
    @GetMapping(path = "/{id}", version = "2.0")
    public ProductV2 getProductV2(
            @Parameter(description = "Alphanumeric product identifier", required = true, example = "prod-123")
                    @PathVariable
                    @NotBlank(message = "Product ID cannot be blank")
                    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Product ID must be alphanumeric")
                    String id) {
        return new ProductV2(id, "Coffee Maker", 4999, USD, IN_STOCK);
    }
}
