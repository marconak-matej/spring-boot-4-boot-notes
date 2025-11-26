package io.github.mm.flyway.product.rest;

import io.github.mm.flyway.product.ProductService;
import io.github.mm.flyway.product.internal.ProductMapper;
import io.github.mm.flyway.product.rest.dto.ProductRequest;
import io.github.mm.flyway.product.rest.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductApi {

    private final ProductService service;
    private final ProductMapper mapper;

    public ProductApi(ProductService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieves a paginated list of all products from the database",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-indexed)",
                        example = "0",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "integer", defaultValue = "0")),
                @Parameter(
                        name = "size",
                        description = "Number of items per page",
                        example = "20",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "integer", defaultValue = "20")),
                @Parameter(
                        name = "sort",
                        description =
                                "Sorting criteria in format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.",
                        example = "name,asc",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "string"))
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval of products",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PagedModel.class),
                                        examples = @ExampleObject(name = "Paginated Products Response", value = """
                                            {
                                              "content": [
                                                {
                                                  "id": 1,
                                                  "name": "Sample Product",
                                                  "description": "Product description",
                                                  "price": 99.99,
                                                  "status": "ACTIVE",
                                                  "category": "ELECTRONICS"
                                                }
                                              ],
                                              "page": {
                                                "size": 20,
                                                "number": 0,
                                                "totalElements": 100,
                                                "totalPages": 5
                                              }
                                            }
                                            """)))
            })
    public PagedModel<@NonNull ProductResponse> getAllProducts(
            @PageableDefault @Parameter(hidden = true) Pageable pageable) {
        var page = service.findAll(pageable);
        return mapper.toPageModel(pageable, page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    public ProductResponse getProductById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product", description = "Creates a new product")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        var product = mapper.toEntity(request);
        var savedProduct = service.create(product);
        return mapper.toResponse(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        var product = mapper.toEntity(id, request);
        var updatedProduct = service.update(id, product);
        return mapper.toResponse(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product", description = "Deletes a product by ID")
    public void deleteProduct(@PathVariable Long id) {
        service.deleteById(id);
    }
}
