package com.example.api.chapter;

import com.example.api.activity.task.RequirementForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterRequirementForm {
    private Long chapterId;
    private Boolean isBlocked;
    private List<RequirementForm> requirements;
}
