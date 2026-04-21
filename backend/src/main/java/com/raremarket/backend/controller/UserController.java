package com.raremarket.backend.controller;

import com.raremarket.backend.dto.UserResponse;
import com.raremarket.backend.dto.ProfileUpdateRequest;
import com.raremarket.backend.model.User;
import com.raremarket.backend.service.UserService;
import com.raremarket.backend.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        boolean registered = userService.register(user);
        if (!registered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid registration data or user already exists");
        }

        User savedUser = userService.authenticate(user.getEmail(), user.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User created but could not be authenticated"));

        return ResponseEntity.ok(UserResponse.from(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        String identifier = loginRequest.getUsername() != null && !loginRequest.getUsername().isBlank()
                ? loginRequest.getUsername()
                : loginRequest.getEmail();

        User user = userService.authenticate(identifier, loginRequest.getPassword()).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(UserResponse.from(user));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    @PutMapping(value = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable String id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) Boolean clearAvatar,
            @RequestParam(required = false) MultipartFile avatar
    ) {
        Optional<User> existingUserOpt = userService.findById(id);
        User existingUser = existingUserOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String previousAvatarUrl = existingUser.getAvatarUrl();

        ProfileUpdateRequest user = new ProfileUpdateRequest();
        user.setUsername(username);
        user.setEmail(email);
        user.setLocation(location);
        user.setPhone(phone);
        user.setBio(bio);

        SupabaseStorageService.UploadResult uploadedAvatar = null;
        try {
            if (avatar != null && !avatar.isEmpty()) {
                uploadedAvatar = supabaseStorageService.uploadAvatar(avatar, id);
                user.setAvatarUrl(uploadedAvatar.publicUrl());
            } else if (Boolean.TRUE.equals(clearAvatar)) {
                user.setAvatarUrl(null);
            }

            User updated = userService.updateProfile(id, user, Boolean.TRUE.equals(clearAvatar))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            boolean shouldDeletePrevious = (avatar != null && !avatar.isEmpty()) || Boolean.TRUE.equals(clearAvatar);
            if (shouldDeletePrevious && previousAvatarUrl != null && !previousAvatarUrl.isBlank()) {
                String updatedAvatarUrl = updated.getAvatarUrl();
                if (updatedAvatarUrl == null || !previousAvatarUrl.equals(updatedAvatarUrl)) {
                    String previousPath = supabaseStorageService.extractStoragePathFromPublicUrl(previousAvatarUrl);
                    if (previousPath != null && !previousPath.isBlank()) {
                        try {
                            supabaseStorageService.deleteAvatar(previousPath);
                        } catch (RuntimeException cleanupEx) {
                            System.err.println("No se pudo borrar el avatar anterior: " + cleanupEx.getMessage());
                        }
                    }
                }
            }

            return ResponseEntity.ok(UserResponse.from(updated));
        } catch (IllegalArgumentException ex) {
            if (uploadedAvatar != null) {
                try {
                    supabaseStorageService.deleteAvatar(uploadedAvatar.storagePath());
                } catch (RuntimeException cleanupEx) {
                    System.err.println("No se pudo limpiar el avatar subido: " + cleanupEx.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalStateException ex) {
            if (uploadedAvatar != null) {
                try {
                    supabaseStorageService.deleteAvatar(uploadedAvatar.storagePath());
                } catch (RuntimeException cleanupEx) {
                    System.err.println("No se pudo limpiar el avatar subido: " + cleanupEx.getMessage());
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, ex.getMessage());
        }
    }
}
