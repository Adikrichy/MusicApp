package com.example.musicapp.Fragment;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.Adapter.SongAdapter;
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.auth.AuthInterceptor;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView trendingArtistsRecycler;
    private RecyclerView trendingMusicRecycler;

    private MediaPlayer mediaPlayer;
    private List<SongDTO> songList;
    private int currentSongIndex = -1;
    private boolean isPlaying = false;

    private MaterialButton playButton, nextBtn, previousBtn;
    private Slider seekBar;
    private TextView songTitleTextView;
    private TextView songArtistTextView;
    private TextView startTimeTextView, endTimeTextView;
    private Handler handler = new Handler();

    private static final String TAG = "PLAYER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        trendingArtistsRecycler = view.findViewById(R.id.recycler_trending_artists);
        trendingMusicRecycler = view.findViewById(R.id.songRecyclerView);
        playButton = view.findViewById(R.id.play_button);
        nextBtn = view.findViewById(R.id.next_button);
        previousBtn = view.findViewById(R.id.prev_button);
        seekBar = view.findViewById(R.id.song_progress_slider);
        startTimeTextView = view.findViewById(R.id.current_time);
        endTimeTextView = view.findViewById(R.id.total_time);
        songTitleTextView = view.findViewById(R.id.songTitleTextView);
        songArtistTextView = view.findViewById(R.id.songArtistTextView);

        setupTrendingArtists();
        setupTrendingMusic();

        return view;
    }

    private void setupTrendingArtists() {
        List<String> artists = java.util.Arrays.asList("Artist 1", "Artist 2", "Artist 3");
        trendingArtistsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingArtistsRecycler.setAdapter(new com.example.musicapp.Adapter.SimpleTextAdapter(artists));
    }

    private void setupTrendingMusic() {
        Log.d(TAG, "Setting up trending music...");
        RetrofitService retrofitService = RetrofitService.getInstance(requireContext());
        SongAPI songAPI = retrofitService.createService(SongAPI.class);

        trendingMusicRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d(TAG, "Making API call to get top 10 songs...");
        songAPI.getTop10Songs().enqueue(new Callback<List<SongDTO>>() {
            @Override
            public void onResponse(Call<List<SongDTO>> call, retrofit2.Response<List<SongDTO>> response) {
                Log.d(TAG, "API Response received. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response successful, songs count: " + response.body().size());
                    songList = response.body();
                    SongAdapter adapter = new SongAdapter(requireContext(), songList, song -> {
                        if (isPlaying) {
                            stopMusic();
                        }
                        currentSongIndex = songList.indexOf(song);
                        updateSongInfo(song);
                        Log.d(TAG, "Selected song: " + song.getTitle() + " by " + song.getArtist());
                        startMusic(song.getFileUrl());
                    });
                    trendingMusicRecycler.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Response not successful. Code: " + response.code());
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to fetch songs! Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SongDTO>> call, Throwable t) {
                Log.e(TAG, "API Call failed", t);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        playButton.setOnClickListener(v -> {
            if (currentSongIndex == -1 || songList == null || songList.isEmpty()) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Please select the song!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (mediaPlayer == null) {
                startMusic(songList.get(currentSongIndex).getFileUrl());
            } else if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setIconResource(R.drawable.ic_play);
                Log.d(TAG, "Paused");
            } else {
                mediaPlayer.start();
                isPlaying = true;
                playButton.setIconResource(R.drawable.ic_pause);
                startUpdatingSeekBar();
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (songList == null || songList.isEmpty()) return;

            if (isPlaying) {
                stopMusic();
            }
            currentSongIndex = (currentSongIndex + 1) % songList.size();
            updateSongInfo(songList.get(currentSongIndex));
            startMusic(songList.get(currentSongIndex).getFileUrl());
        });

        previousBtn.setOnClickListener(v -> {
            if (songList == null || songList.isEmpty()) return;

            if (isPlaying) {
                stopMusic();
            }
            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
            updateSongInfo(songList.get(currentSongIndex));
            startMusic(songList.get(currentSongIndex).getFileUrl());
        });

        seekBar.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && mediaPlayer != null) {
                int seekPosition = (int) (mediaPlayer.getDuration() * (value / 100));
                mediaPlayer.seekTo(seekPosition);
            }
        });
    }

    private void updateSongInfo(SongDTO song) {
        songTitleTextView.setText(song.getTitle());
        songArtistTextView.setText(song.getArtist());
    }

    private void startMusic(String url) {
        releasePlayer();
        
        // Проверяем и форматируем URL
        if (!url.startsWith("http")) {
            // Если URL относительный, добавляем базовый URL
            url = "http://10.0.2.2:8080/song/file/" + url;
        } else if (url.contains("docs.google.com")) {
            // Если это Google Drive URL, преобразуем его в прямой URL для скачивания
            url = url.replace("uc?export=download&id=", "uc?export=download&confirm=no_antivirus&id=");
        }
        
        final String finalUrl = url;
        
        Log.d(TAG, "Attempting to play from URL: " + finalUrl);
        Log.d(TAG, "URL ends with: " + finalUrl.substring(finalUrl.lastIndexOf(".")));

        // Проверяем наличие токена
        TokenManager tokenManager = new TokenManager(requireContext());
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "No access token found");
            if (isAdded() && getContext() != null) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Please login to access music files", Toast.LENGTH_SHORT).show()
                );
            }
            return;
        }

        // Получаем клиент из RetrofitService
        OkHttpClient client = RetrofitService.getInstance(requireContext()).getOkHttpClient();
        
        // Создаем новый поток для загрузки и воспроизведения
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(finalUrl)
                        .addHeader("Accept", "*/*")
                        .removeHeader("Content-Type")
                        .build();

                Log.d(TAG, "Sending request to download audio file");
                Log.d(TAG, "Request URL: " + request.url());
                Log.d(TAG, "Request method: " + request.method());
                Log.d(TAG, "Request headers: " + request.headers());
                
                okhttp3.Response response = client.newCall(request).execute();
                
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e(TAG, "Failed to download audio file. Code: " + response.code() + ", Error: " + errorBody);
                    Log.e(TAG, "Response headers: " + response.headers());
                    Log.e(TAG, "Response message: " + response.message());
                    
                    // Проверяем, есть ли токен
                    TokenManager tokenManagerInstance = new TokenManager(requireContext());
                    String currentToken = tokenManagerInstance.getAccessToken();
                    Log.e(TAG, "Current access token: " + (currentToken != null ? "present" : "null"));
                    
                    if (isAdded() && getContext() != null) {
                        requireActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), "Failed to download audio file: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                    return;
                }

                // Получаем временный файл для аудио
                File tempFile = File.createTempFile("audio", ".mp3", requireContext().getCacheDir());
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(response.body().bytes());
                }

                Log.d(TAG, "Audio file downloaded successfully to: " + tempFile.getAbsolutePath());
                
                // Воспроизводим локальный файл
                requireActivity().runOnUiThread(() -> setupMediaPlayer(tempFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.e(TAG, "Error downloading audio file: " + e.getMessage());
                e.printStackTrace();
                if (isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Error downloading audio file: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        }).start();
    }

    private void setupMediaPlayer(String filePath) {
        try {
            releasePlayer();
            
            mediaPlayer = new MediaPlayer();
            Log.d(TAG, "MediaPlayer created");
            
            // Используем AudioAttributes вместо setAudioStreamType
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
            Log.d(TAG, "AudioAttributes set");

            // Устанавливаем слушатели до setDataSource
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "MediaPlayer prepared successfully");
                try {
                    if (!mp.isPlaying()) {
                        Log.d(TAG, "Starting playback");
                        mp.start();
                        isPlaying = true;
                        playButton.setIconResource(R.drawable.ic_pause);

                        seekBar.setValue(0);
                        seekBar.setValueFrom(0);
                        seekBar.setValueTo(100);

                        int totalDuration = mp.getDuration();
                        Log.d(TAG, "Total duration: " + totalDuration + "ms");
                        endTimeTextView.setText(formatTime(totalDuration));

                        startUpdatingSeekBar();
                        Log.d(TAG, "Playing: " + filePath);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error starting playback: " + e.getMessage());
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Error starting playback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error playing audio: " + what, Toast.LENGTH_SHORT).show();
                }
                releasePlayer();
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "Playback completed");
                isPlaying = false;
                playButton.setIconResource(R.drawable.ic_play);
                handler.removeCallbacksAndMessages(null);
                seekBar.setValue(0);
                startTimeTextView.setText(formatTime(0));
                releasePlayer();
            });

            // Устанавливаем источник данных
            Log.d(TAG, "Setting data source: " + filePath);
            mediaPlayer.setDataSource(filePath);
            Log.d(TAG, "Data source set, preparing async");
            
            // Подготавливаем MediaPlayer асинхронно
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Exception while preparing MediaPlayer: " + e.getMessage());
            e.printStackTrace();
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Error preparing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            releasePlayer();
        }
    }

    private void startUpdatingSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    if (duration > 0) {
                        float progress = (currentPosition * 100f) / duration;
                        seekBar.setValue(Math.min(progress, 100f));
                        startTimeTextView.setText(formatTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            }
        }, 0);
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            playButton.setIconResource(R.drawable.ic_play);
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            try {
                Log.d(TAG, "Releasing MediaPlayer");
                if (mediaPlayer.isPlaying()) {
                    Log.d(TAG, "Stopping playback");
                    mediaPlayer.stop();
                }
                Log.d(TAG, "Resetting MediaPlayer");
                mediaPlayer.reset();
                Log.d(TAG, "Releasing MediaPlayer resources");
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing MediaPlayer: " + e.getMessage());
            } finally {
                mediaPlayer = null;
                isPlaying = false;
                playButton.setIconResource(R.drawable.ic_play);
                Log.d(TAG, "MediaPlayer released");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        handler.removeCallbacksAndMessages(null);
    }
}

