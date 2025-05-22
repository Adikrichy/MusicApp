package com.example.musicapp.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.R;
import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.api.UserAPI;
import com.example.musicapp.dto.UserDTO;
import com.example.musicapp.ultis.RetrofitService;

public class ProfileFragment extends Fragment
{
    private TextView tvUsername;
    private TextView tvEmail;

    public ProfileFragment()
    {

    }

    public static ProfileFragment newInstance(String username, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);


        ListView listView = view.findViewById(R.id.profile_options_list);
        String[] options = new String[]{
                "Favourites",
                "Downloads",
                "Languages",
                "Follow",
                "Log Out"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item_profile,
                R.id.text1,
                options
        );
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = options[position];
                if ("Log Out".equals(selectedItem)) {

                    showLogoutDialog();
                }
            }
        });

        // Найдите кнопку Edit Profile по id editProfile
        View editProfileButton = view.findViewById(R.id.editProfile);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), com.example.musicapp.activity.EditProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Получить актуальные данные пользователя с сервера
        UserAPI userAPI = RetrofitService.getInstance(requireContext()).createService(UserAPI.class);
        userAPI.getProfile().enqueue(new retrofit2.Callback<UserDTO>() {
            @Override
            public void onResponse(retrofit2.Call<UserDTO> call, retrofit2.Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();
                    tvUsername.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                    // TODO: обновить аватар, если нужно
                }
            }
            @Override
            public void onFailure(retrofit2.Call<UserDTO> call, Throwable t) {
                // Можно показать ошибку
            }
        });
    }

    private void showLogoutDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        ImageButton btnCloseDialog = dialog.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Button btnConfirmLogout = dialog.findViewById(R.id.btn_confirm_logout);
        btnConfirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                TokenManager tokenManager = new TokenManager(requireContext());
                tokenManager.getAccessToken();


                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}
