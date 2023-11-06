package com.example.api.activity.task.graphtask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGraphTaskChapterForm {
    @Schema(required = true) private Long chapterId;
    @Schema(required = true) private CreateGraphTaskForm form;
}
