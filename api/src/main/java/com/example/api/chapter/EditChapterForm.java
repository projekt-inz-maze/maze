package com.example.api.chapter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditChapterForm {
    @Schema(required = true) private Long chapterId;
    @Schema(required = true) private ChapterForm editionForm;
}
