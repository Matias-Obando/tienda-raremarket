package com.raremarket.backend.dto;

public class AuthResponse {
    private String token;
    private UserResponse user;

    public static AuthResponse of(String token, UserResponse user) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(user);
        return response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
