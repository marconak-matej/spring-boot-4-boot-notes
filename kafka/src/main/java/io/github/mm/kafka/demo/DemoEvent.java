package io.github.mm.kafka.demo;

import jakarta.validation.constraints.NotBlank;

public record DemoEvent(@NotBlank String id, @NotBlank String message) {}
