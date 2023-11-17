package com.example.api.course;

import com.example.api.user.hero.HeroType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveCourseForm {
    @Schema(required = true) private String name;
    @Schema private String description;
    @Schema(required = true) private List<CourseHeroDTO> heroes;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseHeroDTO {
        @Schema(required = true) HeroType type;
        @Schema(required = true) private Double value;
        @Schema(required = true) private Long coolDownMillis;
    }
}
