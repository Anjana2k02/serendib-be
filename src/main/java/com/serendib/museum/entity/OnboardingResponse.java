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
import java.util.Set;

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
     * Question 2: User type - linked via UserType entity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    /**
     * Question 3: Interests (multi-select) - linked via Category entities
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "onboarding_response_categories",
        joinColumns = @JoinColumn(name = "response_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> interests;

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
