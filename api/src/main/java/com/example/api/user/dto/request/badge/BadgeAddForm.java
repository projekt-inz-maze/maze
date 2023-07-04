package com.example.api.user.dto.request.badge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BadgeAddForm extends BadgeForm{
    @Schema(required = true) private BadgeType type;
    @Schema(required = true) private Long courseId;
}
