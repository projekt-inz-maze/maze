package com.example.api.activity.task.dto.response.result.summary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AverageGrade {
    private String chapterName;
    private List<AverageGradeForChapter> avgGradesForChapter;
}
