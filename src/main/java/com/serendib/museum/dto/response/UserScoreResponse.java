package com.serendib.museum.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user score responses.
 * Contains full user score information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User score response with full details")
public class UserScoreResponse {

    @Schema(description = "Score record ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category name", example = "Ancient Artifacts")
    private String categoryName;

    @Schema(description = "Current score value", example = "85.5")
    private Double current;

    @Schema(description = "Dwell time in milliseconds", example = "120000")
    private Long dwellTime;

    @Schema(description = "Creation timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T14:30:00")
    private LocalDateTime updatedAt;
}
