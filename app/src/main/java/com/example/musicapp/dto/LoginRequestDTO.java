package com.example.musicapp.dto;

public class LoginRequestDTO {
    private String username;
    private String password;

    public LoginRequestDTO(String username, String password){
        this.username = username;
        this.password = password;
    }
}
