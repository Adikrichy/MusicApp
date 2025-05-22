package com.example.musicapp.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;

public class LyricsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        TextView title = findViewById(R.id.lyricsTitle);
        TextView artist = findViewById(R.id.lyricsArtist);
        TextView lyrics = findViewById(R.id.lyricsText);
        ImageButton backButton = findViewById(R.id.backButton);

        title.setText(getIntent().getStringExtra("title"));
        artist.setText(getIntent().getStringExtra("artist"));
        lyrics.setText(getIntent().getStringExtra("lyrics"));

        backButton.setOnClickListener(v -> finish());
    }
}