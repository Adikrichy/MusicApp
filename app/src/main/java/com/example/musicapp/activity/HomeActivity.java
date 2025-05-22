package com.example.musicapp.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.musicapp.Fragment.PremiumFragment;
import com.example.musicapp.Fragment.SubsciptionFragment;
import com.example.musicapp.R;
import com.example.musicapp.Fragment.HomeFragment;
import com.example.musicapp.Fragment.ProfileFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        username = getIntent().getStringExtra("username");
        if (username == null) username = "Username";
        email = getIntent().getStringExtra("email");
        if (email == null) email = "Email";


        bottomNavigationView = findViewById(R.id.bottom_navigation);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home)
                {
                    selectedFragment = new HomeFragment();
                }

                else if (itemId == R.id.nav_profile)
                {
                    selectedFragment = ProfileFragment.newInstance(username, email);
                }

                else if (itemId == R.id.nav_premium)
                {
                    selectedFragment = new PremiumFragment();
                }

                else if (itemId == R.id.nav_subscribed)
                {
                    selectedFragment = new SubsciptionFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}