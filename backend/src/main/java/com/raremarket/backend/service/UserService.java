package com.raremarket.backend.service;

import com.raremarket.backend.model.User;
import com.raremarket.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        String encoded = passwordEncoder.encode(user.getPassword());
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(encoded); // Para compatibilidad legacy
        user.setPasswordHash(encoded); // Para compatibilidad con columna password_hash
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
}
