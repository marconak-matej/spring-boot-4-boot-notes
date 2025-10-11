package io.github.mm.versioning.product.domain;

public enum Currency {
    USD("USD", "$"),
    EUR("EUR", "€"),
    GBP("GBP", "£");

    private final String code;
    private final String symbol;

    Currency(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }
}
