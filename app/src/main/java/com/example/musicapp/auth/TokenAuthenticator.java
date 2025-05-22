package com.example.musicapp.auth;

import androidx.annotation.NonNull;

import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.dto.RefreshTokenRequestDTO;
import com.example.musicapp.dto.LoginResponseDTO;
import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;

public class TokenAuthenticator implements Authenticator {
    private final TokenManager tokenManager;
    private final LoginAPI authApi;

    public TokenAuthenticator(TokenManager tokenManager, Retrofit retrofit) {
        this.tokenManager = tokenManager;
        this.authApi = retrofit.create(LoginAPI.class);
    }

    @Override
    public Request authenticate(Route route,@NonNull Response response) throws IOException {

        if (responseCount(response) >= 2) {
            tokenManager.clear();
            return null;
        }

        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            tokenManager.clear();
            return null;
        }


        RefreshTokenRequestDTO refreshReq = new RefreshTokenRequestDTO(refreshToken);
        Call<LoginResponseDTO> call = authApi.refreshToken(refreshReq);
        retrofit2.Response<LoginResponseDTO> resp = call.execute();

        if (resp.isSuccessful() && resp.body() != null) {
            String newAccess = resp.body().getAccessToken();
            String newRefresh = resp.body().getRefreshToken();
            tokenManager.saveTokens(newAccess, newRefresh);


            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newAccess)
                    .build();
        }


        tokenManager.clear();
        return null;
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) {
            count++;
        }
        return count;
    }
}

