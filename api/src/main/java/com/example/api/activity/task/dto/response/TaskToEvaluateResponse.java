package com.example.api.activity.task.dto.response;


import com.example.api.activity.task.dto.response.util.FileResponse;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskToEvaluateResponse {
    private String userEmail;
    private Long fileTaskResponseId;
    private String firstName;
    private String lastName;
    private String activityName;
    private Boolean isLate;
    private String activityDetails;
    private String userAnswer;
    private List<FileResponse> file;
    private Double maxPoints;
    private Long fileTaskId;
    private Long remaining;


    public TaskToEvaluateResponse(User user,
                                  Long fileTaskResponseId,
                                  String activityName,
                                  boolean isLate,
                                  String activityDetails,
                                  String userAnswer,
                                  List<FileResponse> file,
                                  Double maxPoints,
                                  Long fileTaskId,
                                  Long remaining) {
        this.userEmail = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fileTaskResponseId = fileTaskResponseId;
        this.activityName = activityName;
        this.isLate = isLate;
        this.activityDetails = activityDetails;
        this.userAnswer = userAnswer;
        this.file = file;
        this.maxPoints = maxPoints;
        this.fileTaskId = fileTaskId;
        this.remaining = remaining;
    }
}
