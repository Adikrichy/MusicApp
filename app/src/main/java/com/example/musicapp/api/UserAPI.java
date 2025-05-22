package com.example.musicapp.api;

import com.example.musicapp.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("/api/user/profile")
    Call<UserDTO> getProfile();

    @PUT("/api/user/update")
    Call<UserDTO> updateProfile(@Body UserDTO userDTO);
} 