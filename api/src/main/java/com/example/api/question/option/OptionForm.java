package com.example.api.question.option;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionForm {
    @Schema(required = true) private String content;
    @Schema(required = true) private Boolean isCorrect;
}
