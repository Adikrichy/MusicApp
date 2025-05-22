// SongAPI.java
package com.example.musicapp.api;

import com.example.musicapp.dto.SongDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SongAPI {
    @GET("/song")
    Call<List<SongDTO>> getAllSongs();

    @GET("/song/trending")
    Call<List<SongDTO>> getTop10Songs();

    @PUT("/song/{id}/like")
    Call<Void> likeSong(@Path("id") Long songId);

    @PUT("/song/{id}/dislike")
    Call<Void> dislikeSong(@Path("id") Long songId);

    @PUT("/song/{id}/view")
    Call<Void> incrementView(@Path("id") Long songId);
}