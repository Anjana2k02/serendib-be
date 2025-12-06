package com.serendib.museum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a museum artifact.
 * Sample entity demonstrating CRUD operations with auditing.
 */
@Entity
@Table(name = "artifacts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artifact extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "date_acquired")
    private LocalDate dateAcquired;

    @Column(name = "estimated_value")
    private Double estimatedValue;

    @Column(name = "is_on_display")
    private Boolean isOnDisplay = false;

    @Column(name = "location_in_museum")
    private String locationInMuseum;
}
