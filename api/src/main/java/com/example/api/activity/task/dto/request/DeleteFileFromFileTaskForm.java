package com.example.api.activity.task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteFileFromFileTaskForm {
    @Schema(required = true) private Long fileTaskId;
    @Schema(required = true) private String studentEmail;
    @Schema(required = true) private int index;
}
