package io.github.mm.versioning.product.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Product(
        String id,
        String title,
        @JsonProperty("price_usd") Double priceUsd,
        @JsonProperty("is_available") Boolean isAvailable) {}
