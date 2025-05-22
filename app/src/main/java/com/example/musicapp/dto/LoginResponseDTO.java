package com.example.musicapp.dto;

public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String message;
    private String email;

    public String getAccessToken() {
        return accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

}
