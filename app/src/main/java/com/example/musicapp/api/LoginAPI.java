package com.example.musicapp.api;

import com.example.musicapp.dto.LoginRequestDTO;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.dto.RefreshTokenRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginAPI {
    @POST("/api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);

    @POST("/api/auth/refresh-token")
    Call<LoginResponseDTO> refreshToken(@Body RefreshTokenRequestDTO Request);
}
