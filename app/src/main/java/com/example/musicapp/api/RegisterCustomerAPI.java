package com.example.musicapp.api;

import com.example.musicapp.dto.RegisterRequestDTO;
import com.example.musicapp.dto.RegisterResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterCustomerAPI {

    @POST("/api/auth/register-customer")
    Call<RegisterResponseDTO> registerCustomer(@Body RegisterRequestDTO request);

//    @GET("/api/auth/verify-email")
//    Call<Void> verifyEmail(@Query("token") String token);


}
