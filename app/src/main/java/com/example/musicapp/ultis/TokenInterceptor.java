package com.example.musicapp.ultis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.dto.RefreshTokenRequestDTO;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class TokenInterceptor implements Interceptor {

    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        SharedPreferences prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();


        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }

        Response response = chain.proceed(requestBuilder.build());


        if (response.code() == 401) {
            response.close();

            String refreshToken = prefs.getString("refresh_token", null);
            if (refreshToken != null) {

                Retrofit retrofit = new Retrofit.Builder()

                        .baseUrl("http://192.168.94.51:8080/")

                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                LoginAPI loginAPI = retrofit.create(LoginAPI.class);

                Call<LoginResponseDTO> call = loginAPI.refreshToken(new RefreshTokenRequestDTO(refreshToken));
                retrofit2.Response<LoginResponseDTO> refreshResponse = call.execute();

                if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {

                    LoginResponseDTO tokenDTO = refreshResponse.body();
                    prefs.edit()
                            .putString("access_token", tokenDTO.getAccessToken())
                            .putString("refresh_token", tokenDTO.getRefreshToken())
                            .apply();


                    Request newRequest = request.newBuilder()
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .build();

                    return chain.proceed(newRequest);
                } else {

                    prefs.edit().clear().apply();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        }

        return response;
    }
}
