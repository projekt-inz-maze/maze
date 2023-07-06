package com.example.api.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveGroupForm {
    @Schema(required = true) private String name;
    @Schema(required = true) private String invitationCode;
    @Schema(required = true) private Long courseId;
}
