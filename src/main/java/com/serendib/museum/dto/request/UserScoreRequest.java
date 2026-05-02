package com.serendib.museum.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user score creation and update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User score request payload for creation and updates")
public class UserScoreRequest {

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category", example = "1", required = true)
    private Long categoryId;

    @NotNull(message = "Current score is required")
    @Schema(description = "Current score value", example = "85.5", required = true)
    private Double current;

    @NotNull(message = "Dwell time is required")
    @PositiveOrZero(message = "Dwell time must be zero or positive")
    @Schema(description = "Dwell time in milliseconds", example = "120000", required = true)
    private Long dwellTime;
}
