package com.example.api.map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterForm {
    @Schema(required = true) private String name;
    @Schema(required = true) private Integer sizeX;
    @Schema(required = true) private Integer sizeY;
    @Schema(required = true) private Long imageId;
    @Schema(required = true) private Integer posX;
    @Schema(required = true) private Integer posY;
    @Schema(required = true) private Long courseId;
}
