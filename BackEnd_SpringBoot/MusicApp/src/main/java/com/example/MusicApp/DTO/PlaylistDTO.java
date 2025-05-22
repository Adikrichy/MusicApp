package com.example.MusicApp.DTO;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistDTO {
    private Long id;
    private String name;
    private List<Long> songIds;
}
