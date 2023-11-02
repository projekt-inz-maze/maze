package com.example.api.activity;

import com.example.api.activity.info.EditInfoForm;
import com.example.api.activity.survey.EditSurveyForm;
import com.example.api.activity.task.filetask.EditFileTaskForm;
import com.example.api.activity.task.graphtask.EditGraphTaskForm;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "activityType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EditGraphTaskForm.class, name = "EXPEDITION"),
        @JsonSubTypes.Type(value = EditFileTaskForm.class, name = "TASK"),
        @JsonSubTypes.Type(value = EditSurveyForm.class, name = "SURVEY"),
        @JsonSubTypes.Type(value = EditInfoForm.class, name = "INFO")
})
public abstract class EditActivityForm {
    @Schema(required = true) private Long activityID;
    @Schema(required = true) private ActivityType activityType;
    @Schema(required = true) private CreateActivityForm activityBody;
}
