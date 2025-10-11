package io.github.mm.versioning.product.domain;

public record ProductV2(String id, String title, Integer price, Currency currency, ProductV2Status status) {
}
