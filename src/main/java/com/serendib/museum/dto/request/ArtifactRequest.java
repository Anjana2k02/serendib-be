package com.serendib.museum.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for artifact creation and update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Artifact request payload for creation and updates")
public class ArtifactRequest {

    @NotBlank(message = "Artifact name is required")
    @Schema(description = "Name of the artifact", example = "Ancient Vase", required = true)
    private String name;

    @Schema(description = "Detailed description of the artifact", example = "A beautifully preserved ceramic vase from the 15th century")
    private String description;

    @NotBlank(message = "Category is required")
    @Schema(description = "Category of the artifact", example = "Ceramics", required = true)
    private String category;

    @Schema(description = "Country of origin", example = "Sri Lanka")
    private String originCountry;

    @Schema(description = "Date when artifact was acquired by the museum", example = "2024-01-15")
    private LocalDate dateAcquired;

    @Positive(message = "Estimated value must be positive")
    @Schema(description = "Estimated monetary value in USD", example = "50000.00")
    private Double estimatedValue;

    @NotNull(message = "Display status is required")
    @Schema(description = "Whether the artifact is currently on display", example = "true", required = true)
    private Boolean isOnDisplay;

    @Schema(description = "Location within the museum", example = "Gallery A, Section 3")
    private String locationInMuseum;
}
