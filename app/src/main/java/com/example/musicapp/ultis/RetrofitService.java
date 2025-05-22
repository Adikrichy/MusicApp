package com.example.musicapp.ultis;

import android.content.Context;
import android.util.Log;

import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.auth.AuthInterceptor;
import com.example.musicapp.auth.TokenAuthenticator;
import com.example.musicapp.auth.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String TAG = "RetrofitService";
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static RetrofitService instance;
    private final Retrofit retrofit;
    private final OkHttpClient client;
    private TokenManager tokenManager;

    private RetrofitService(Context context) {
        Log.d(TAG, "Initializing RetrofitService with base URL: " + BASE_URL);
        tokenManager = new TokenManager(context);

        // Создаем простой Retrofit для TokenAuthenticator
        Retrofit simpleRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Добавляем логирование HTTP-запросов
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Настраиваем таймауты
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(tokenManager))
                .authenticator(new TokenAuthenticator(tokenManager, simpleRetrofit))
                .build();

        // Создаем основной Retrofit с настроенным клиентом
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitService getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitService(context);
        }
        return instance;
    }

    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public OkHttpClient getOkHttpClient() {
        return client;
    }
}
