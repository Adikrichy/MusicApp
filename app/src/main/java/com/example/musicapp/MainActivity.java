package com.example.musicapp;

import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Bundle;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.activity.HomeActivity;
import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.auth.TokenManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);


        TokenManager tokenManager = new TokenManager(this);


        String accessToken = tokenManager.getAccessToken();

        Intent intent;
        if (accessToken != null) {

            intent = new Intent(MainActivity.this, HomeActivity.class);
        } else {

            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();

    }
}