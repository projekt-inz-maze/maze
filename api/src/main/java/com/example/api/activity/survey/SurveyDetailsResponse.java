package com.example.api.activity.survey;

import com.example.api.activity.result.dto.response.SurveyResultInfoResponse;
import com.example.api.file.FileResponse;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SurveyDetailsResponse {
    private String name;
    private String description;
    private Double experience;
    private SurveyResultInfoResponse feedback;
    private List<FileResponse> files;

    public SurveyDetailsResponse(Survey survey) {
        this.name = survey.getTitle();
        this.description = survey.getDescription();
        this.experience = survey.getMaxPoints();
        this.files = survey
                .getFiles().stream().map(file -> new FileResponse(file.getId(), file.getName())).toList();
    }
}
