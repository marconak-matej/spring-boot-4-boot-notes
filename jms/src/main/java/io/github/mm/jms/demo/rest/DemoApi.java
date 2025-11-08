package io.github.mm.jms.demo.rest;

import io.github.mm.jms.demo.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "JMS Messages", description = "Operations for sending messages to JMS queues using Artemis")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Send message to JMS queue",
            description = "Publishes a message to the configured Artemis JMS queue asynchronously")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "202", description = "Message accepted for processing"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid message format",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Message to send to JMS queue",
                            required = true,
                            content = @Content(schema = @Schema(implementation = Demo.class)))
                    @RequestBody
                    @Valid
                    Demo message) {
        service.sendMessage(message);
    }
}
