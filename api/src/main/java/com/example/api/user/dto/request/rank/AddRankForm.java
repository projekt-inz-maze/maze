package com.example.api.user.dto.request.rank;

import com.example.api.user.hero.HeroType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRankForm {
    @Schema(required = true) private String name;
    @Schema(required = true) private Double minPoints;
    @Schema(required = true) private MultipartFile image;
    @Schema(required = true) private HeroType type;
    @Schema(required = true) private Long courseId;
}
