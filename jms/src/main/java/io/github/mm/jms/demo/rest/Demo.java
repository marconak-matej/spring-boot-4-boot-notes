package io.github.mm.jms.demo.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Message to be sent to JMS queue")
public record Demo(
        @NotBlank(message = "Message must not be blank")
        @Size(max = 50, message = "Message must not exceed 50 characters")
        @Schema(description = "Message content", example = "Hello JMS!", required = true, maxLength = 50)
        String message) {}
