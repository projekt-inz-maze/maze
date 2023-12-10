package com.example.api.file.image;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterImageResponse {
    private Long id;
    private String name;
    private ImageType type;
}
