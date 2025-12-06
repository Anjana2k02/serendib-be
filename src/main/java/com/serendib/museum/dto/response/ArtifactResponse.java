package com.serendib.museum.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for artifact responses.
 * Contains full artifact information including audit fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Artifact response with full details")
public class ArtifactResponse {

    @Schema(description = "Artifact ID", example = "1")
    private Long id;

    @Schema(description = "Name of the artifact", example = "Ancient Vase")
    private String name;

    @Schema(description = "Detailed description", example = "A beautifully preserved ceramic vase from the 15th century")
    private String description;

    @Schema(description = "Category", example = "Ceramics")
    private String category;

    @Schema(description = "Country of origin", example = "Sri Lanka")
    private String originCountry;

    @Schema(description = "Date acquired", example = "2024-01-15")
    private LocalDate dateAcquired;

    @Schema(description = "Estimated value in USD", example = "50000.00")
    private Double estimatedValue;

    @Schema(description = "Display status", example = "true")
    private Boolean isOnDisplay;

    @Schema(description = "Location in museum", example = "Gallery A, Section 3")
    private String locationInMuseum;

    @Schema(description = "Creation timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Created by user", example = "admin@museum.com")
    private String createdBy;

    @Schema(description = "Last updated by user", example = "curator@museum.com")
    private String updatedBy;
}
