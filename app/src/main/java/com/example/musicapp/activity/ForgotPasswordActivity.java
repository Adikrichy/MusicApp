package com.example.musicapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.musicapp.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtCode;
    private Button btnSendCode, btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        edtCode = findViewById(R.id.edtCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());
    }

    private void sendCode() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else {
            btnSendCode.setEnabled(false);  // Disable the button
            startCountdownTimer();          // Start countdown to re-enable it

            new Thread(() -> {
                try {
                    URL url = new URL("http://192.168.3.20:8080/api/auth/send-code");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String jsonInput = "{\"email\":\"" + email + "\"}";

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInput.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Failed to send code. Response code: " + responseCode, Toast.LENGTH_LONG).show()
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }).start();
        }
    }



    private void verifyCode() {
        String email = edtEmail.getText().toString().trim();
        String code = edtCode.getText().toString().trim();

        if (email.isEmpty() || code.isEmpty()) {
            Toast.makeText(this, "Please enter both email and code", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.102.51:8080/api/auth/verify-code");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInput = "{\"email\":\"" + email + "\",\"code\":\"" + code + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Code verified successfully!", Toast.LENGTH_SHORT).show();

                        // ✅ START the reset password activity
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email); // pass email forward
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }


    private void startCountdownTimer() {
        new android.os.CountDownTimer(60000, 1000) { // 60 seconds

            public void onTick(long millisUntilFinished) {
                btnSendCode.setText("Wait " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("Send Code");
            }

        }.start();
    }

}
