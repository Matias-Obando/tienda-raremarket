package com.raremarket.backend.service;

import com.raremarket.backend.model.PasswordResetToken;
import com.raremarket.backend.model.User;
import com.raremarket.backend.repository.PasswordResetTokenRepository;
import com.raremarket.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetService Tests")
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private String frontendBaseUrl = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository,
                tokenRepository,
                mailService,
                passwordEncoder,
                frontendBaseUrl
        );

        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("hashedPassword");
        testUser.setPasswordHash("hashedPassword");
    }

    @Test
    @DisplayName("Should request password reset for valid email")
    void testRequestPasswordReset_ValidEmail() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(testUser));

        // Act
        passwordResetService.requestPasswordReset(email);

        // Assert
        verify(userRepository).findByEmail(email.toLowerCase());
        verify(tokenRepository).deleteByUserIdAndUsedAtIsNull(testUser.getId());
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(mailService).sendPasswordResetEmail(eq(testUser.getEmail()), anyString());
    }

    @Test
    @DisplayName("Should handle password reset with non-existent user")
    void testRequestPasswordReset_NonExistentUser() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        // Act
        passwordResetService.requestPasswordReset(email);

        // Assert
        verify(userRepository).findByEmail(email.toLowerCase());
        verify(tokenRepository, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void testRequestPasswordReset_NormalizeEmail() {
        // Arrange
        String email = "TEST@EXAMPLE.COM";
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        passwordResetService.requestPasswordReset(email);

        // Assert
        verify(userRepository).findByEmail("test@example.com");
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void testRequestPasswordReset_NullEmail() {
        // Act
        passwordResetService.requestPasswordReset(null);

        // Assert
        verify(userRepository, never()).findByEmail(anyString());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle blank email gracefully")
    void testRequestPasswordReset_BlankEmail() {
        // Act
        passwordResetService.requestPasswordReset("   ");

        // Assert
        verify(userRepository, never()).findByEmail(anyString());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reset password with valid token")
    void testResetPassword_ValidToken() {
        // Arrange
        String rawToken = "valid-token-123";
        String newPassword = "newPassword123";
        String hashedPassword = "hashed_newPassword123";

        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID().toString());
        token.setUserId(testUser.getId());
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        token.setUsedAt(null);

        when(tokenRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                anyString(), any(Instant.class))).thenReturn(Optional.of(token));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(hashedPassword);

        // Act
        passwordResetService.resetPassword(rawToken, newPassword);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(hashedPassword, savedUser.getPassword());
        assertEquals(hashedPassword, savedUser.getPasswordHash());

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getUsedAt());
    }

    @Test
    @DisplayName("Should throw exception for null token")
    void testResetPassword_NullToken() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword(null, "newPassword")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("token is required", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception for blank token")
    void testResetPassword_BlankToken() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword("   ", "newPassword")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception for null password")
    void testResetPassword_NullPassword() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword("token", null)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception for blank password")
    void testResetPassword_BlankPassword() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword("token", "   ")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should throw exception for invalid or expired token")
    void testResetPassword_InvalidToken() {
        // Arrange
        String rawToken = "invalid-token";
        when(tokenRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                anyString(), any(Instant.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword(rawToken, "newPassword")
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid or expired reset token", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testResetPassword_UserNotFound() {
        // Arrange
        String rawToken = "valid-token";
        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID().toString());
        token.setUserId("non-existent-user-id");
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));

        when(tokenRepository.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                anyString(), any(Instant.class))).thenReturn(Optional.of(token));
        when(userRepository.findById("non-existent-user-id")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword(rawToken, "newPassword")
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    @DisplayName("Should trim whitespace from email")
    void testRequestPasswordReset_TrimEmail() {
        // Arrange
        String email = "  test@example.com  ";
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        passwordResetService.requestPasswordReset(email);

        // Assert
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should propagate when MailService fails to send email")
    void testRequestPasswordReset_MailServiceThrows() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(testUser));
        doThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Brevo down"))
                .when(mailService).sendPasswordResetEmail(anyString(), anyString());

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> passwordResetService.requestPasswordReset(email));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should propagate when token repository save fails")
    void testRequestPasswordReset_TokenRepositoryThrows() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("DB error")).when(tokenRepository).save(any(PasswordResetToken.class));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.requestPasswordReset(email));
        assertTrue(ex.getMessage().contains("DB error"));
        // mailService should not be called if save failed
        verify(mailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @Disabled("Intentional failing test - disabled for CI")
    @DisplayName("Intentional failing test in PasswordResetServiceTest")
    void intentionalFail_PasswordReset() {
        fail("Prueba intencional que debe fallar en PasswordResetServiceTest");
    }

    // Helper method to hash token (same logic as service)
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashed.length * 2);
            for (byte current : hashed) {
                hex.append(String.format(java.util.Locale.ROOT, "%02x", current));
            }
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible", exception);
        }
    }
}
