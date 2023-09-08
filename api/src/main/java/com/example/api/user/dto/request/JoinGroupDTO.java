package com.example.api.user.dto.request;

import com.example.api.user.hero.HeroType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinGroupDTO {
    @Schema(required = true) private String invitationCode;
    @Schema(required = true) private HeroType heroType;
}
