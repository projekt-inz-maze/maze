package com.example.api.user.dto.request;

import com.example.api.user.model.HeroType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHeroForm {
    @Schema(required = true) private HeroType type;
    @Schema(required = false) private Double value;
    @Schema(required = false) private Long coolDownMillis;
}
