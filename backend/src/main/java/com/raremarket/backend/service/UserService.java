package com.raremarket.backend.service;

import com.raremarket.backend.model.User;
import com.raremarket.backend.repository.UserRepository;
import com.raremarket.backend.dto.ProfileUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public boolean register(User user) {
        if (user == null) {
            return false;
        }

        String normalizedEmail = normalizeEmail(user.getEmail());
        if (normalizedEmail == null) {
            return false;
        }

        String normalizedUsername = normalizeUsername(user.getUsername(), normalizedEmail);
        if (normalizedUsername == null) {
            return false;
        }

        if (userRepository.findByUsername(normalizedUsername).isPresent() ||
            userRepository.findByEmail(normalizedEmail).isPresent()) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return false;
        }

        // Generar UUID si no tiene ID
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }

        String encoded = passwordEncoder.encode(user.getPassword());
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(encoded); // Para compatibilidad legacy
        user.setPasswordHash(encoded); // Para compatibilidad con columna password_hash
        // Ensure role is set; default to 'user' if none provided
        String providedRole = blankToNull(user.getRole());
        user.setRole(providedRole != null ? providedRole.trim().toLowerCase() : "user");
        userRepository.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String identifier, String rawPassword) {
        if (identifier == null || identifier.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Optional.empty();
        }

        String normalizedIdentifier = identifier.trim();
        Optional<User> userOpt = userRepository.findByUsername(normalizedIdentifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(normalizedIdentifier.toLowerCase(Locale.ROOT));
        }

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        String storedHash = firstNonBlank(user.getPasswordHash(), user.getPassword());
        if (storedHash == null) {
            return Optional.empty();
        }

        // Compatibilidad: si el valor guardado es texto plano legacy, permite login y se conserva compatibilidad.
        if (rawPassword.equals(storedHash) || passwordEncoder.matches(rawPassword, storedHash)) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(String userId) {
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    @Transactional
    public Optional<User> updateProfile(String userId, ProfileUpdateRequest profileData, boolean clearAvatar) {
        if (userId == null || userId.isBlank() || profileData == null) {
            return Optional.empty();
        }

        Optional<User> existingOpt = userRepository.findById(userId);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        User existing = existingOpt.get();

        String normalizedEmail = normalizeEmail(profileData.getEmail());
        if (normalizedEmail == null) {
            return Optional.empty();
        }

        String normalizedUsername = normalizeUsername(profileData.getUsername(), normalizedEmail);
        if (normalizedUsername == null) {
            return Optional.empty();
        }

        userRepository.findByUsername(normalizedUsername)
            .filter(user -> !user.getId().equals(userId))
            .ifPresent(user -> { throw new IllegalArgumentException("Username already exists"); });

        userRepository.findByEmail(normalizedEmail)
            .filter(user -> !user.getId().equals(userId))
            .ifPresent(user -> { throw new IllegalArgumentException("Email already exists"); });

        existing.setUsername(normalizedUsername);
        existing.setEmail(normalizedEmail);
        if (profileData.getAvatarUrl() != null) {
            existing.setAvatarUrl(blankToNull(profileData.getAvatarUrl()));
        } else if (clearAvatar) {
            existing.setAvatarUrl(null);
        }
        existing.setLocation(blankToNull(profileData.getLocation()));
        existing.setPhone(blankToNull(profileData.getPhone()));
        existing.setBio(blankToNull(profileData.getBio()));

        return Optional.of(userRepository.save(existing));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username, String email) {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return null;
        }
        String fallback = email.substring(0, atIndex).trim();
        return fallback.isEmpty() ? null : fallback;
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
