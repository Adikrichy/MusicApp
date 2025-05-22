package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.service.SongService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
//@RequestMapping("/api/songs")
@CrossOrigin
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping("/song")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/song/trending")
    public List<SongDTO> getTrendingSongs() {
        return songService.getTop10TrendingSongs();
    }

    @PutMapping("/song/{id}/like")
    public void likeSong(@PathVariable Long id) {
        songService.likeSong(id);
    }

    @PutMapping("/song/{id}/dislike")
    public void dislikeSong(@PathVariable Long id) {
        songService.dislikeSong(id);
    }

    @PutMapping("/song/{id}/view")
    public void incrementView(@PathVariable Long id) {
        songService.incrementView(id);
    }

    @GetMapping("/song/file/{id}")
    public ResponseEntity<InputStreamResource> getSongFile(@PathVariable Long id) {
        // Получаем Google Drive ссылку из базы
        String googleDriveUrl = songService.getFileUrlById(id); // например: https://docs.google.com/uc?export=download&id=...

        try {
            // Открываем соединение с Google Drive
            URL url = new URL(googleDriveUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // иногда требуется

            // Проверяем ответ
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return ResponseEntity.status(responseCode).build();
            }

            // Получаем имя файла (можно парсить из Content-Disposition, если нужно)
            String fileName = "audio.mp3";

            InputStream inputStream = connection.getInputStream();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
