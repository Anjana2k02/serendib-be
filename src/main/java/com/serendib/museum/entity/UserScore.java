package com.serendib.museum.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user's score for a specific category.
 * Tracks the current score and dwell time spent by a user in a category.
 */
@Entity
@Table(name = "user_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Reference to the category
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Current score value for this user-category pair
     */
    @Column(name = "current", nullable = false)
    private Double current = 0.0;

    /**
     * Dwell time in milliseconds spent by the user in this category
     */
    @Column(name = "dwell_time", nullable = false)
    private Long dwellTime = 0L;

    /**
     * Timestamp when the record was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
