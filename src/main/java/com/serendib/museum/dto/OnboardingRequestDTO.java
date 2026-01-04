package com.serendib.museum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for receiving onboarding survey data from frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequestDTO {

    /**
     * Question 1: Visitor type
     */
    @NotBlank(message = "Visitor type is required")
    @Size(max = 50, message = "Visitor type must not exceed 50 characters")
    private String visitorType;

    /**
     * Country (required only for foreign visitors)
     */
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    /**
     * Question 2: User type
     */
    @NotBlank(message = "User type is required")
    @Size(max = 50, message = "User type must not exceed 50 characters")
    private String userType;

    /**
     * Question 3: Interests (multi-select)
     */
    @NotNull(message = "Interests are required")
    @Size(min = 1, message = "At least one interest must be selected")
    private List<String> interests;

    /**
     * Question 4: Time preference
     */
    @NotBlank(message = "Time preference is required")
    @Size(max = 50, message = "Time preference must not exceed 50 characters")
    private String timePreference;

    /**
     * Question 5: Language preference
     */
    @NotBlank(message = "Language preference is required")
    @Size(max = 50, message = "Language preference must not exceed 50 characters")
    private String languagePreference;
}
