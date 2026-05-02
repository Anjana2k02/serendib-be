package com.serendib.museum.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.serendib.museum.dto.request.GoogleAuthRequest;
import com.serendib.museum.dto.request.LoginRequest;
import com.serendib.museum.dto.request.RegisterRequest;
import com.serendib.museum.dto.response.AuthenticationResponse;
import com.serendib.museum.dto.response.UserResponse;
import com.serendib.museum.entity.User;
import com.serendib.museum.enums.Role;
import com.serendib.museum.exception.DuplicateResourceException;
import com.serendib.museum.repository.UserRepository;
import com.serendib.museum.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling authentication operations.
 * Manages user registration, login, Google OAuth, and token generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client-id}")
    private String googleClientId;

    /**
     * Registers a new user in the system.
     *
     * @param request registration request containing user details
     * @return authentication response with JWT tokens
     * @throws DuplicateResourceException if email already exists
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        // Save user to database
        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Build response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserResponse(savedUser))
                .build();
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param request login request containing credentials
     * @return authentication response with JWT tokens
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Build response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    /**
     * Authenticates a user via Google ID token.
     * Verifies the token with Google, then finds or creates the local user account.
     *
     * @param request Google auth request containing the ID token from the Flutter client
     * @return authentication response with JWT tokens
     * @throws RuntimeException if the Google ID token is invalid or verification fails
     */
    @Transactional
    public AuthenticationResponse authenticateWithGoogle(GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = verifyGoogleToken(request.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        // Ensure names are not null
        if (firstName == null) firstName = email.split("@")[0];
        if (lastName == null) lastName = "";

        // Look up user by googleId first, then by email
        Optional<User> existingByGoogleId = userRepository.findByGoogleId(googleId);
        Optional<User> existingByEmail = userRepository.findByEmail(email);

        User user;

        if (existingByGoogleId.isPresent()) {
            // User already linked with this Google account
            user = existingByGoogleId.get();
        } else if (existingByEmail.isPresent()) {
            // Email exists (registered via email/password) — link Google account
            user = existingByEmail.get();
            user.setGoogleId(googleId);
            user = userRepository.save(user);
        } else {
            // New user — create account from Google profile
            String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(randomPassword)
                    .role(Role.USER)
                    .googleId(googleId)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();
            user = userRepository.save(user);
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshToken the refresh token from the client
     * @return new authentication response with fresh access token, or null if token invalid
     */
    @Transactional(readOnly = true)
    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            String userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail == null) {
                log.warn("Refresh token has no subject claim");
                return null;
            }

            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                log.warn("User not found for refresh token email: {}", userEmail);
                return null;
            }

            if (!jwtService.isTokenValid(refreshToken, user)) {
                log.warn("Refresh token is invalid or expired for user: {}", userEmail);
                return null;
            }

            String newAccessToken = jwtService.generateAccessToken(user);
            log.info("Issued new access token for user: {}", userEmail);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // reuse the same refresh token
                    .tokenType("Bearer")
                    .user(mapToUserResponse(user))
                    .build();
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Verifies a Google ID token and returns its payload.
     *
     * @param idToken the Google ID token string from the client
     * @return verified token payload
     * @throws RuntimeException if the token is invalid or verification fails
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }
            return googleIdToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to verify Google ID token: " + e.getMessage(), e);
        }
    }

    /**
     * Maps User entity to UserResponse DTO.
     *
     * @param user user entity
     * @return user response DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }
}
