package com.example.api.activity.task.dto.response;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivitiesResponse {
    private Long id;
    private String name;
    private String chapterName;
    private ActivityType type;

    public ActivitiesResponse(Activity activity, String chapterName) {
        this.id = activity.getId();
        this.name = activity.getTitle();
        this.chapterName = chapterName;
        this.type = activity.getActivityType();
    }
}
