package com.example.api.activity.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSurveyChapterForm {
    @Schema(required = true) private Long chapterId;
    @Schema(required = true) private CreateSurveyForm form;
}
