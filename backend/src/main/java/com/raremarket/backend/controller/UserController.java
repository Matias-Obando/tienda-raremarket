package com.raremarket.backend.controller;

import com.raremarket.backend.dto.UserResponse;
import com.raremarket.backend.model.User;
import com.raremarket.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

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
}
