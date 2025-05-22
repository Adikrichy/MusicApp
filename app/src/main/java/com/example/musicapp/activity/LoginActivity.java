package com.example.musicapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;


import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.dto.LoginRequestDTO;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        tokenManager = new TokenManager(this);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> {
            // Start the ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        btnLogin.setOnClickListener(v -> login());


        btnGoToSignUp.setOnClickListener(v -> GoToRegisterActivity());


        btnTogglePassword.setOnClickListener(v -> TogglePassword());
    }

    private void TogglePassword() {
        if (isPasswordVisible) {

            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye);
            isPasswordVisible = false;
        } else {

            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = true;
        }


        edtPassword.setSelection(edtPassword.getText().length());
    }


    private void GoToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }


        LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);


        LoginAPI loginAPI = RetrofitService.getInstance(this).createService(LoginAPI.class);

        loginAPI.login(loginRequest).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call, @NonNull Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO loginResponse = response.body();
                    String accessToken = response.body().getAccessToken();
                    String refreshToken = response.body().getRefreshToken();
                    String message = response.body().getMessage();
                    String email = loginResponse.getEmail();


                    if (message != null && !message.equals("Login successfully")) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }


                    tokenManager.saveTokens(accessToken, refreshToken);


                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email != null ? email : "unknown@example.com");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials or try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Unable to connect to the server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goToHomePage() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
