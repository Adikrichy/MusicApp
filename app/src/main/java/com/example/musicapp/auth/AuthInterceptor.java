package com.example.musicapp.auth;

import androidx.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tm) {
        this.tokenManager = tm;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String accessToken = tokenManager.getAccessToken();

        Log.d(TAG, "Intercepting request to: " + original.url());
        Log.d(TAG, "Access token present: " + (accessToken != null));

        if (accessToken == null) {
            Log.w(TAG, "No access token available, proceeding without authorization");
            return chain.proceed(original);
        }

        Request requestWithToken = original.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        Log.d(TAG, "Added Authorization header to request");
        Response response = chain.proceed(requestWithToken);
        
        Log.d(TAG, "Response code: " + response.code());
        if (response.code() == 403) {
            Log.e(TAG, "Access forbidden. Token might be invalid or expired");
        }

        return response;
    }
}

