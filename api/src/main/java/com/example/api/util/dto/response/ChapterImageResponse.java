package com.example.api.util.dto.response;

import com.example.api.util.model.ImageType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterImageResponse {
    private Long id;
    private String name;
    private ImageType type;
}
