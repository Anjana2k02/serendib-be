package com.serendib.museum.controller;

import com.serendib.museum.dto.request.ArtifactRequest;
import com.serendib.museum.dto.response.ArtifactResponse;
import com.serendib.museum.exception.ErrorResponse;
import com.serendib.museum.service.ArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for artifact management.
 * Provides CRUD operations for museum artifacts.
 */
@RestController
@RequestMapping("/api/v1/artifacts")
@RequiredArgsConstructor
@Tag(name = "Artifacts", description = "Endpoints for managing museum artifacts")
@SecurityRequirement(name = "bearerAuth")
public class ArtifactController {

    private final ArtifactService artifactService;

    /**
     * Creates a new artifact.
     *
     * @param request artifact creation request
     * @return created artifact
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CURATOR')")
    @Operation(
            summary = "Create new artifact",
            description = "Creates a new artifact in the museum collection. Requires ADMIN or CURATOR role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Artifact successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtifactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ArtifactResponse> createArtifact(
            @Valid @RequestBody ArtifactRequest request
    ) {
        ArtifactResponse response = artifactService.createArtifact(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all artifacts with pagination.
     *
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sortBy field to sort by (default: id)
     * @param sortDir sort direction (default: asc)
     * @return page of artifacts
     */
    @GetMapping
    @Operation(
            summary = "Get all artifacts",
            description = "Retrieves a paginated list of all artifacts in the museum collection."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved artifacts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Page<ArtifactResponse>> getAllArtifacts(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ArtifactResponse> artifacts = artifactService.getAllArtifacts(pageable);
        return ResponseEntity.ok(artifacts);
    }

    /**
     * Retrieves a specific artifact by ID.
     *
     * @param id artifact ID
     * @return artifact details
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get artifact by ID",
            description = "Retrieves detailed information about a specific artifact."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved artifact",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtifactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artifact not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ArtifactResponse> getArtifactById(
            @Parameter(description = "Artifact ID") @PathVariable Long id
    ) {
        ArtifactResponse response = artifactService.getArtifactById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing artifact.
     *
     * @param id artifact ID
     * @param request artifact update request
     * @return updated artifact
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CURATOR')")
    @Operation(
            summary = "Update artifact",
            description = "Updates an existing artifact. Requires ADMIN or CURATOR role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Artifact successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtifactResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artifact not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ArtifactResponse> updateArtifact(
            @Parameter(description = "Artifact ID") @PathVariable Long id,
            @Valid @RequestBody ArtifactRequest request
    ) {
        ArtifactResponse response = artifactService.updateArtifact(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an artifact (soft delete).
     *
     * @param id artifact ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete artifact",
            description = "Soft deletes an artifact from the collection. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Artifact successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artifact not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteArtifact(
            @Parameter(description = "Artifact ID") @PathVariable Long id
    ) {
        artifactService.deleteArtifact(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches artifacts by name.
     *
     * @param searchTerm search term
     * @param page page number
     * @param size page size
     * @return page of matching artifacts
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search artifacts by name",
            description = "Searches for artifacts whose names contain the search term (case-insensitive)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved search results",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Page<ArtifactResponse>> searchArtifacts(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ArtifactResponse> artifacts = artifactService.searchArtifacts(searchTerm, pageable);
        return ResponseEntity.ok(artifacts);
    }
}
