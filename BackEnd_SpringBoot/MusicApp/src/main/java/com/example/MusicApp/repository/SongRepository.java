package com.example.MusicApp.repository;

import com.example.MusicApp.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop10ByOrderByViewsDesc();
}

