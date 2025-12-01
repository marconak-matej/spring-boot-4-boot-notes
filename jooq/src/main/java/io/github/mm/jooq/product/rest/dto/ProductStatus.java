package io.github.mm.jooq.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a product in the catalog")
public enum ProductStatus {
    @Schema(description = "Product is active and available for sale")
    ACTIVE,
    @Schema(description = "Product is temporarily inactive")
    INACTIVE,
    @Schema(description = "Product has been discontinued and is no longer available")
    DISCONTINUED
}
