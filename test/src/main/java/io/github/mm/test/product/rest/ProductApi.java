package io.github.mm.test.product.rest;

import io.github.mm.test.product.ProductNotFoundException;
import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing product catalog")
public class ProductApi {

    private final ProductService service;

    public ProductApi(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new product",
            description =
                    "Creates a new product with the specified name and price. Returns the created product with a generated ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Product successfully created",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request - name is blank or price is negative",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Product createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Product details including name and price",
                            required = true,
                            content = @Content(schema = @Schema(implementation = CreateProductRequest.class)))
                    @RequestBody
                    CreateProductRequest request) {
        return service.createProduct(request.name(), request.price());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a product by its unique identifier. Returns 404 if product is not found.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product found",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Product getProductById(
            @Parameter(description = "Product ID", required = true, example = "1") @PathVariable Long id) {
        return service.getProductById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description =
                    "Retrieves a list of all products in the catalog. Returns an empty list if no products exist.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of products retrieved successfully",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Product.class)))
            })
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update product price",
            description = "Updates the price of an existing product. Returns the updated product with the new price.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product price updated successfully",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid price - must be positive",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Product updatePrice(
            @Parameter(description = "Product ID", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "New price for the product",
                            required = true,
                            content = @Content(schema = @Schema(implementation = UpdatePriceRequest.class)))
                    @RequestBody
                    UpdatePriceRequest request) {
        return service.updatePrice(id, request.price());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product from the catalog by its ID. Returns 204 No Content on success.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public void deleteProduct(
            @Parameter(description = "Product ID to delete", required = true, example = "1") @PathVariable Long id) {
        service.deleteProduct(id);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @Schema(description = "Request payload for creating a new product")
    public record CreateProductRequest(
            @Schema(description = "Product name", example = "Laptop", required = true)
            String name,

            @Schema(description = "Product price in USD", example = "999.99", required = true, minimum = "0")
            Double price) {}

    @Schema(description = "Request payload for updating product price")
    public record UpdatePriceRequest(
            @Schema(description = "New price in USD", example = "899.99", required = true, minimum = "0")
            Double price) {}

    @Schema(description = "Error response with a descriptive message")
    public record ErrorResponse(
            @Schema(
                    description = "Error message explaining what went wrong",
                    example = "Product not found with id: 123")
            String message) {}
}
