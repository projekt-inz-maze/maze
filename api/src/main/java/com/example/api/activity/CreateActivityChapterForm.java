package com.example.api.activity;

import com.example.api.activity.submittask.CreateSubmitTaskForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateActivityChapterForm {
    @Schema(required = true) private Long chapterId;
    @Schema(required = true) private CreateSubmitTaskForm form;
}
