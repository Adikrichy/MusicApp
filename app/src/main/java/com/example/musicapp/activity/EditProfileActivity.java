package com.example.musicapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.api.UserAPI;
import com.example.musicapp.dto.UserDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.io.IOException;

import retrofit2.Call;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView avatarImage;
    private EditText editUsername, editEmail;
    private Button saveButton, pickAvatarButton;
    private Uri avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        avatarImage = findViewById(R.id.avatarImage);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        saveButton = findViewById(R.id.saveButton);
        pickAvatarButton = findViewById(R.id.pickAvatarButton);

        pickAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString();
            String newEmail = editEmail.getText().toString();
            // Для аватара можно добавить avatarUrl, если реализуете загрузку

            UserDTO userDTO = new UserDTO(newUsername, newEmail, null);

            // Получаем id пользователя (например, из SharedPreferences или TokenManager)
            Long userId = getCurrentUserId(); // реализуйте этот метод

            UserAPI userAPI = RetrofitService.getInstance(this).createService(UserAPI.class);
            userAPI.updateProfile(userDTO).enqueue(new retrofit2.Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, retrofit2.Response<UserDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatarUri);
                avatarImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Добавьте метод для получения id пользователя
    private Long getCurrentUserId() {
        // TODO: Получите id пользователя из SharedPreferences, TokenManager или другого источника
        // Например:
        // return TokenManager.getInstance(this).getUserId();
        return 1L; // временно для теста
    }
} 