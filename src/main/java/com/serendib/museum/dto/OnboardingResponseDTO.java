package com.serendib.museum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning onboarding survey data to frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponseDTO {

    private Long id;
    private Long userId;
    private String visitorType;
    private String country;
    private String userType;
    private List<String> interests;
    private String timePreference;
    private String languagePreference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
