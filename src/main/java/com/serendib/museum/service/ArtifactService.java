package com.serendib.museum.service;

import com.serendib.museum.dto.request.ArtifactRequest;
import com.serendib.museum.dto.response.ArtifactResponse;
import com.serendib.museum.entity.Artifact;
import com.serendib.museum.exception.ResourceNotFoundException;
import com.serendib.museum.repository.ArtifactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for artifact management.
 * Provides CRUD operations and business logic for artifacts.
 */
@Service
@RequiredArgsConstructor
public class ArtifactService {

    private final ArtifactRepository artifactRepository;

    /**
     * Creates a new artifact.
     *
     * @param request artifact creation request
     * @return created artifact response
     */
    @Transactional
    public ArtifactResponse createArtifact(ArtifactRequest request) {
        Artifact artifact = Artifact.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .originCountry(request.getOriginCountry())
                .dateAcquired(request.getDateAcquired())
                .estimatedValue(request.getEstimatedValue())
                .isOnDisplay(request.getIsOnDisplay())
                .locationInMuseum(request.getLocationInMuseum())
                .build();

        Artifact savedArtifact = artifactRepository.save(artifact);
        return mapToResponse(savedArtifact);
    }

    /**
     * Retrieves all artifacts with pagination.
     *
     * @param pageable pagination information
     * @return page of artifact responses
     */
    @Transactional(readOnly = true)
    public Page<ArtifactResponse> getAllArtifacts(Pageable pageable) {
        return artifactRepository.findByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Retrieves a specific artifact by ID.
     *
     * @param id artifact ID
     * @return artifact response
     * @throws ResourceNotFoundException if artifact not found
     */
    @Transactional(readOnly = true)
    public ArtifactResponse getArtifactById(Long id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact", "id", id));

        if (artifact.getDeleted()) {
            throw new ResourceNotFoundException("Artifact", "id", id);
        }

        return mapToResponse(artifact);
    }

    /**
     * Updates an existing artifact.
     *
     * @param id      artifact ID
     * @param request artifact update request
     * @return updated artifact response
     * @throws ResourceNotFoundException if artifact not found
     */
    @Transactional
    public ArtifactResponse updateArtifact(Long id, ArtifactRequest request) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact", "id", id));

        if (artifact.getDeleted()) {
            throw new ResourceNotFoundException("Artifact", "id", id);
        }

        artifact.setName(request.getName());
        artifact.setDescription(request.getDescription());
        artifact.setCategory(request.getCategory());
        artifact.setOriginCountry(request.getOriginCountry());
        artifact.setDateAcquired(request.getDateAcquired());
        artifact.setEstimatedValue(request.getEstimatedValue());
        artifact.setIsOnDisplay(request.getIsOnDisplay());
        artifact.setLocationInMuseum(request.getLocationInMuseum());

        Artifact updatedArtifact = artifactRepository.save(artifact);
        return mapToResponse(updatedArtifact);
    }

    /**
     * Soft deletes an artifact by setting deleted flag to true.
     *
     * @param id artifact ID
     * @throws ResourceNotFoundException if artifact not found
     */
    @Transactional
    public void deleteArtifact(Long id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact", "id", id));

        artifact.setDeleted(true);
        artifactRepository.save(artifact);
    }

    /**
     * Searches artifacts by name.
     *
     * @param searchTerm search term
     * @param pageable   pagination information
     * @return page of matching artifact responses
     */
    @Transactional(readOnly = true)
    public Page<ArtifactResponse> searchArtifacts(String searchTerm, Pageable pageable) {
        return artifactRepository.searchByName(searchTerm, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Finds artifacts by category.
     *
     * @param category artifact category
     * @param pageable pagination information
     * @return page of artifact responses
     */
    @Transactional(readOnly = true)
    public Page<ArtifactResponse> getArtifactsByCategory(String category, Pageable pageable) {
        return artifactRepository.findByCategory(category, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Maps Artifact entity to ArtifactResponse DTO.
     *
     * @param artifact artifact entity
     * @return artifact response DTO
     */
    private ArtifactResponse mapToResponse(Artifact artifact) {
        return ArtifactResponse.builder()
                .id(artifact.getId())
                .name(artifact.getName())
                .description(artifact.getDescription())
                .category(artifact.getCategory())
                .originCountry(artifact.getOriginCountry())
                .dateAcquired(artifact.getDateAcquired())
                .estimatedValue(artifact.getEstimatedValue())
                .isOnDisplay(artifact.getIsOnDisplay())
                .locationInMuseum(artifact.getLocationInMuseum())
                .createdAt(artifact.getCreatedAt())
                .updatedAt(artifact.getUpdatedAt())
                .createdBy(artifact.getCreatedBy())
                .updatedBy(artifact.getUpdatedBy())
                .build();
    }
}
