package io.github.mm.versioning.product.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product model (API version 2.0) - Enhanced with integer price and status")
public record ProductV2(
        @Schema(description = "Unique product identifier", example = "prod-123")
        String id,

        @Schema(description = "Product title", example = "Coffee Maker")
        String title,

        @Schema(description = "Price in cents", example = "4999")
        Integer price,

        @Schema(description = "Currency code", example = "USD")
        Currency currency,

        @Schema(description = "Product status", example = "IN_STOCK")
        ProductV2Status status) {}
