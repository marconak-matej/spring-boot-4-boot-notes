package io.github.mm.kafka.demo.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Demo message to be sent to Kafka topic")
public record Demo(
        @NotBlank @Schema(description = "Unique message identifier", example = "msg-123", required = true)
        String id,

        @NotBlank @Schema(description = "Message content", example = "Hello Kafka!", required = true)
        String message) {}
