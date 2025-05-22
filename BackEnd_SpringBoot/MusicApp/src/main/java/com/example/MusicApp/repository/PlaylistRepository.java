package com.example.MusicApp.repository;

import com.example.MusicApp.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByCustomerId(Long customerId);
}

