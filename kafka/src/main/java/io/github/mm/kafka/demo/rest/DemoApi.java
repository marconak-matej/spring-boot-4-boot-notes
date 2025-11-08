package io.github.mm.kafka.demo.rest;

import io.github.mm.kafka.demo.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demos")
@Tag(name = "Kafka Demos", description = "Operations for sending messages to Kafka topics")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Send message to Kafka",
            description = "Publishes a demo message to the configured Kafka topic asynchronously")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "202", description = "Message accepted for processing"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request body",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(
            @RequestBody(description = "Demo message to send", required = true)
                    @Valid
                    @org.springframework.web.bind.annotation.RequestBody
                    Demo demo) {
        service.sendMessage(demo.id(), demo.message());
    }
}
