package io.github.mm.jooq.product.rest;

import io.github.mm.jooq.product.ProductService;
import io.github.mm.jooq.product.rest.dto.CreateProductRequest;
import io.github.mm.jooq.product.rest.dto.Product;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.product.rest.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management API - CRUD operations using jOOQ with PostgreSQL")
public class ProductApi {

    private final ProductService productService;

    public ProductApi(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the catalog with the provided details. The SKU must be unique.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Product successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input - validation errors or duplicate SKU",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Product details to create",
                            required = true)
                    @Valid
                    @RequestBody
                    CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves detailed information about a specific product by its unique identifier")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public Product getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true, example = "1") @PathVariable
                    Long id) {
        return productService.getProductById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List all products",
            description = "Retrieves a paginated list of products with optional filtering by status and category. "
                    + "Supports sorting and pagination parameters.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved product list",
                        content = @Content(mediaType = "application/json"))
            })
    public PagedModel<@NonNull Product> getAllProducts(
            @PageableDefault @Parameter(hidden = true) Pageable pageable,
            @Parameter(description = "Filter products by status (ACTIVE, INACTIVE, DISCONTINUED)", example = "ACTIVE")
                    @RequestParam(required = false)
                    ProductStatus status,
            @Parameter(description = "Filter products by category name", example = "Electronics")
                    @RequestParam(required = false)
                    String category) {
        var page = productService.findAll(pageable, status, category);
        return new PagedModel<>(PageableExecutionUtils.getPage(page.getContent(), pageable, page::getTotalElements));
    }

    @PutMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update an existing product",
            description =
                    "Updates an existing product with the provided details. Only non-null fields will be updated.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Product successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Product.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input - validation errors",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public Product updateProduct(
            @Parameter(description = "ID of the product to update", required = true, example = "1") @PathVariable
                    Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Updated product details",
                            required = true)
                    @Valid
                    @RequestBody
                    UpdateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Permanently deletes a product from the catalog by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true, example = "1") @PathVariable
                    Long id) {
        productService.deleteProduct(id);
    }
}
