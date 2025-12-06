package com.serendib.museum.repository;

import com.serendib.museum.entity.Artifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Artifact entity.
 * Provides database operations for artifact management with pagination support.
 */
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

    /**
     * Finds artifacts by category.
     *
     * @param category artifact category
     * @param pageable pagination information
     * @return page of artifacts
     */
    Page<Artifact> findByCategory(String category, Pageable pageable);

    /**
     * Finds artifacts currently on display.
     *
     * @param pageable pagination information
     * @return page of artifacts on display
     */
    Page<Artifact> findByIsOnDisplay(Boolean isOnDisplay, Pageable pageable);

    /**
     * Searches artifacts by name containing the search term (case-insensitive).
     *
     * @param searchTerm search term
     * @param pageable   pagination information
     * @return page of matching artifacts
     */
    @Query("SELECT a FROM Artifact a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND a.deleted = false")
    Page<Artifact> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds all artifacts that are not deleted.
     *
     * @param pageable pagination information
     * @return page of non-deleted artifacts
     */
    Page<Artifact> findByDeletedFalse(Pageable pageable);
}
