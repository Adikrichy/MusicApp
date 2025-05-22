package com.example.MusicApp.service;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.model.Song;
import com.example.MusicApp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist() != null ? song.getArtist().getFullName() : "Unknown",
                        song.getFileUrl(),
                        song.getImageUrl(),
                        song.getLyrics(),
                        song.getLikes(),
                        song.getDislikes(),
                        song.getViews()
                ))
                .collect(Collectors.toList());
    }

    public List<SongDTO> getTop10TrendingSongs() {
        return songRepository.findTop10ByOrderByViewsDesc()
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist() != null ? song.getArtist().getFullName() : "Unknown",
                        song.getFileUrl(),
                        song.getImageUrl(),
                        song.getLyrics(),
                        song.getLikes(),
                        song.getDislikes(),
                        song.getViews()
                ))
                .collect(Collectors.toList());
    }

    public void likeSong(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            song.setLikes(song.getLikes() + 1);
            songRepository.save(song);
        });
    }

    public void dislikeSong(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            song.setDislikes(song.getDislikes() + 1);
            songRepository.save(song);
        });
    }

    public void incrementView(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            song.setViews(song.getViews() + 1);
            songRepository.save(song);
        });
    }
    public String getFileUrlById(Long id) {
        // Предполагается, что у вас есть сущность Song с полем fileUrl
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
        return song.getFileUrl();
    }

}