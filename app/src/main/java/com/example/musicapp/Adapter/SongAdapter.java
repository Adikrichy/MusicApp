package com.example.musicapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.activity.LyricsActivity;
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<SongDTO> songList;
    private OnSongClickListener listener;
    private Context context;
    private SongAPI songAPI;

    public SongAdapter(Context context, List<SongDTO> songList, OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.songAPI = RetrofitService.getInstance(context).createService(SongAPI.class);
    }

    public interface OnSongClickListener {
        void onSongClick(SongDTO song);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongDTO song = songList.get(position);
        holder.titleText.setText(song.getTitle());
        holder.artistText.setText(song.getArtist());
        
        // Проверяем, что URL изображения существует и не является аудиофайлом
        String imageUrl = song.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.contains("docs.google.com")) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.coverImage);
        } else {
            // Если URL изображения недействителен, используем плейсхолдер
            holder.coverImage.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.menuButton);
            popup.inflate(R.menu.song_menu);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_lyrics) {
                    Intent intent = new Intent(context, LyricsActivity.class);
                    intent.putExtra("title", song.getTitle());
                    intent.putExtra("artist", song.getArtist());
                    intent.putExtra("lyrics", song.getLyrics());
                    context.startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_like) {
                    songAPI.likeSong(song.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(context, "Liked " + song.getTitle(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Failed to like", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                } else if (itemId == R.id.menu_dislike) {
                    songAPI.dislikeSong(song.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(context, "Disliked " + song.getTitle(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Failed to dislike", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, artistText;
        ImageView coverImage, menuButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.songTitle);
            artistText = itemView.findViewById(R.id.songArtist);
            coverImage = itemView.findViewById(R.id.songCoverImage);
            menuButton = itemView.findViewById(R.id.menu_button);
        }
    }
}
