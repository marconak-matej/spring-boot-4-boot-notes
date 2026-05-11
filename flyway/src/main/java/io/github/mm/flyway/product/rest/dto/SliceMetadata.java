package io.github.mm.flyway.product.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Slice metadata for nextCursor-based pagination")
public record SliceMetadata(
        @Schema(description = "Whether there are more items to fetch", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext,

        @Schema(description = "Cursor for the next page (last item ID), null if no more items", example = "100")
        String nextCursor,

        @Schema(description = "The size of slice", example = "10")
        Integer size) {}
