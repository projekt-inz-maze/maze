package com.example.api.activity.submittask.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitTaskResultDTO {
    @Schema(required = true) private Long id;
    @Schema(required = true) private String title;
    @Schema(required = true) private String content;
}
