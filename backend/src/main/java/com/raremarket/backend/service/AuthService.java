package com.raremarket.backend.service;

import com.raremarket.backend.dto.AuthRequest;
import com.raremarket.backend.dto.AuthResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final Map<String, String> users = new ConcurrentHashMap<>();

    public AuthService() {
        users.put("demo@raremarket.com", "123456");
    }

    public AuthResponse register(AuthRequest request) {
        if (users.containsKey(request.getEmail())) {
            throw new IllegalArgumentException("Ese email ya esta registrado");
        }
        users.put(request.getEmail(), request.getPassword());
        return tokenFor(request.getEmail());
    }

    public AuthResponse login(AuthRequest request) {
        String savedPassword = users.get(request.getEmail());
        if (savedPassword == null || !savedPassword.equals(request.getPassword())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }
        return tokenFor(request.getEmail());
    }

    private AuthResponse tokenFor(String email) {
        String raw = email + ":raremarket-dev-token";
        String token = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return new AuthResponse(token, email);
    }
}
