package com.example.api.activity.task.dto.response.result.summary;

import com.example.api.map.model.Chapter;
import lombok.Data;

import java.util.List;

@Data
public class AverageGrade {
    private String chapterName;
    private List<AverageGradeForChapter> avgGradesForChapter;

    public AverageGrade(Chapter chapter) {
        this.chapterName = chapter.getName();
    }
}
