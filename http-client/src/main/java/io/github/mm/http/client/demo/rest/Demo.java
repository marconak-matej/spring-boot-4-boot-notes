package io.github.mm.http.client.demo.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Demo entity for HTTP client operations")
public record Demo(
        @Schema(description = "Unique demo identifier", example = "demo-123", accessMode = Schema.AccessMode.READ_ONLY)
        String id,

        @NotBlank(message = "Name must not be blank")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        @Schema(
                description = "Demo name",
                example = "Sample Demo",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50)
        String name) {}
