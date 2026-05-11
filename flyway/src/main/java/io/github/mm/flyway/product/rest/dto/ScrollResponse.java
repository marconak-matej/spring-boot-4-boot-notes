package io.github.mm.flyway.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Scroll response for nextCursor-based pagination")
public record ScrollResponse<T>(
        @Schema(description = "Items in the current slice", requiredMode = Schema.RequiredMode.REQUIRED)
        List<T> content,

        @Schema(description = "Slice metadata for pagination", requiredMode = Schema.RequiredMode.REQUIRED)
        SliceMetadata slice) {}
