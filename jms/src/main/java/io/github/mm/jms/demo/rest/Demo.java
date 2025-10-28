package io.github.mm.jms.demo.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Demo(
        @NotBlank(message = "Message must not be blank")
        @Size(max = 50, message = "Message must not exceed 50 characters")
        String message) {}
