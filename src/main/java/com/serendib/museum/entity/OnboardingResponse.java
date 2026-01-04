package com.serendib.museum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing user's onboarding survey responses
 */
@Entity
@Table(name = "onboarding_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the user who completed the survey
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    /**
     * Question 1: Visitor type - "Local (Sri Lankan)" or "Foreign Visitor"
     */
    @Column(name = "visitor_type", nullable = false, length = 50)
    private String visitorType;

    /**
     * Country (only for foreign visitors)
     */
    @Column(name = "country", length = 100)
    private String country;

    /**
     * Question 2: User type - Student, Teacher, Professor, etc.
     */
    @Column(name = "user_type", nullable = false, length = 50)
    private String userType;

    /**
     * Question 3: Interests (multi-select) - stored as JSON array
     */
    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests; // Stored as JSON string

    /**
     * Question 4: Time preference
     */
    @Column(name = "time_preference", nullable = false, length = 50)
    private String timePreference;

    /**
     * Question 5: Language preference
     */
    @Column(name = "language_preference", nullable = false, length = 50)
    private String languagePreference;

    /**
     * Timestamp when the response was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the response was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
