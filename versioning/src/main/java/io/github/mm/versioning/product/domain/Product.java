package io.github.mm.versioning.product.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product model (API version 1.0)")
public record Product(
        @Schema(description = "Unique product identifier", example = "prod-123")
        String id,

        @Schema(description = "Product title", example = "Coffee Maker")
        String title,

        @JsonProperty("price_usd") @Schema(description = "Price in USD", example = "49.99")
        Double priceUsd,

        @JsonProperty("is_available") @Schema(description = "Product availability status", example = "true")
        Boolean isAvailable) {}
