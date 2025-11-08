package io.github.mm.http.client.demo.rest;

import io.github.mm.http.client.demo.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demos")
@Tag(name = "Demos", description = "CRUD operations for demo entities showcasing HTTP client capabilities")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService demoService) {
        this.service = demoService;
    }

    @Operation(summary = "Create a new demo", description = "Creates a new demo entity with generated ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Demo successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Demo.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request body",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Demo createDemo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Demo object to create",
                            required = true)
                    @Valid
                    @RequestBody
                    Demo demo) {
        return service.createDemo(demo);
    }

    @Operation(summary = "Update an existing demo", description = "Updates a demo entity by ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Demo successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Demo.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request body",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Demo not found", content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @PutMapping("/{id}")
    public Demo updateDemo(
            @Parameter(description = "Demo ID", required = true, example = "demo-123") @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated demo object", required = true)
                    @Valid
                    @RequestBody
                    Demo demo) {
        return service.updateDemo(id, demo);
    }

    @Operation(summary = "Get all demos", description = "Retrieves a list of all demo entities")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved all demos",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = Demo.class)))),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @GetMapping
    public List<Demo> getDemos() {
        return service.getAllDemos();
    }

    @Operation(summary = "Get demo by ID", description = "Retrieves a specific demo entity by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the demo",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Demo.class))),
                @ApiResponse(responseCode = "404", description = "Demo not found", content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @GetMapping("/{id}")
    public Demo getDemo(
            @Parameter(description = "Demo ID", required = true, example = "demo-123") @PathVariable String id) {
        return service.getDemoById(id);
    }

    @Operation(summary = "Delete demo", description = "Deletes a demo entity by ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Demo successfully deleted"),
                @ApiResponse(responseCode = "404", description = "Demo not found", content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDemo(
            @Parameter(description = "Demo ID to delete", required = true, example = "demo-123") @PathVariable
                    String id) {
        service.deleteDemo(id);
    }
}
