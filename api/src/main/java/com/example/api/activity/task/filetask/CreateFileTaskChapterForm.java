package com.example.api.activity.task.filetask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFileTaskChapterForm {
    @Schema(required = true) private Long chapterId;
    @Schema(required = true) private CreateFileTaskForm form;
}
