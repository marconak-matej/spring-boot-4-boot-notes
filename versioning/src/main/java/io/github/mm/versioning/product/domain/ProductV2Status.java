package io.github.mm.versioning.product.domain;

public enum ProductV2Status {
    IN_STOCK("IN_STOCK"),
    OUT_OF_STOCK("OUT_OF_STOCK"),
    DISCONTINUED("DISCONTINUED"),
    COMING_SOON("COMING_SOON");

    private final String value;

    ProductV2Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
