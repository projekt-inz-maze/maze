package com.example.api.activity;

import com.example.api.activity.info.CreateInfoForm;
import com.example.api.activity.survey.CreateSurveyForm;
import com.example.api.activity.submittask.CreateSubmitTaskForm;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "activityType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateSurveyForm.class, name = "SURVEY"),
        @JsonSubTypes.Type(value = CreateInfoForm.class, name = "INFO"),
        @JsonSubTypes.Type(value = CreateSubmitTaskForm.class, name = "SUBMIT")
})
public abstract class CreateActivityForm {
    @Schema(required = true) private ActivityType activityType;
    @Schema(required = true) private String title;
    @Schema(required = true) private String description;
    @Schema(required = true) private Integer posX;
    @Schema(required = true) private Integer posY;

    public CreateActivityForm(Activity activity) {
        this.activityType = activity.getActivityType();
        this.title = activity.getTitle();
        this.description = activity.getDescription();
        this.posX = activity.getPosX();
        this.posY = activity.getPosY();
    }
}
