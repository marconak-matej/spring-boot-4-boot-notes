package io.github.mm.http.client.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Demo(
        String id,

        @NotBlank(message = "Name must not be blank") @Size(max = 50, message = "Name must not exceed 50 characters")
        String name) {}
