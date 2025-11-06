package io.github.mm.kafka.demo.rest;

import jakarta.validation.constraints.NotBlank;

public record Demo(@NotBlank String id, @NotBlank String message) {}
