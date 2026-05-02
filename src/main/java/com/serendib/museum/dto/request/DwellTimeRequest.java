package com.serendib.museum.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reporting user activity near an artifact.
 * Sent by the mobile app when a user is detected as 'standing' near an artifact.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dwell time tracking request - sent when user is standing near an artifact")
public class DwellTimeRequest {

    @NotNull(message = "Artifact ID is required")
    @Schema(description = "ID of the artifact the user is standing near", example = "5", required = true)
    private Long artifactId;

    @NotBlank(message = "Activity is required")
    @Schema(description = "User activity type (e.g., 'standing', 'walking')", example = "standing", required = true)
    private String activity;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be a positive value")
    @Schema(description = "Duration in milliseconds the user has been standing near the artifact since last report", example = "15000", required = true)
    private Long durationMs;
}
