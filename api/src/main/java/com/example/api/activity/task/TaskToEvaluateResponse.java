package com.example.api.activity.task;


import com.example.api.activity.ActivityType;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.file.FileResponse;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskToEvaluateResponse {
    private String userEmail;
    private Long fileTaskResponseId;
    private Long responseId;
    private String firstName;
    private String lastName;
    private String activityName;
    private Boolean isLate;
    private String activityDetails;
    private Optional<String> userTitle;
    private Optional<String> userContent;
    private Optional<String> userAnswer;
    private Optional<List<FileResponse>> file;
    private Double maxPoints;
    private Long fileTaskId;
    private Long activityId;
    private ActivityType activityType;
    private Long remaining;

    public static TaskToEvaluateResponseBuilder builder() {
        return new TaskToEvaluateResponseBuilder();
    }


    public static class TaskToEvaluateResponseBuilder {
        private String userEmail;
        private Long fileTaskResponseId;
        private Long responseId;
        private String firstName;
        private String lastName;
        private String activityName;
        private Boolean isLate;
        private String activityDetails;
        private String userTitle;
        private String userContent;
        private String userAnswer;
        private List<FileResponse> file;
        private Double maxPoints;
        private Long fileTaskId;
        private Long activityId;
        private ActivityType activityType;
        private Long remaining;

        TaskToEvaluateResponseBuilder() {
        }

        public TaskToEvaluateResponseBuilder from(ActivityResult result) {
            this.fileTaskResponseId = result.getId();
            this.responseId = result.getId();
            this.activityName = result.getActivity().getTitle();
            this.activityDetails = result.getActivity().getDescription();
            this.maxPoints = result.getActivity().getMaxPoints();
            this.fileTaskId = result.getActivity().getId();
            this.activityId = result.getActivity().getId();
            this.activityType = result.getActivity().getActivityType();

            return this;
        }

        public TaskToEvaluateResponseBuilder withUser(User user) {
            this.userEmail = user.getEmail();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();

            return this;
        }

        public TaskToEvaluateResponseBuilder withUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public TaskToEvaluateResponseBuilder withFileTaskResponseId(Long fileTaskResponseId) {
            this.fileTaskResponseId = fileTaskResponseId;
            return this;
        }

        public TaskToEvaluateResponseBuilder withResponseId(Long responseId) {
            this.responseId = responseId;
            return this;
        }

        public TaskToEvaluateResponseBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public TaskToEvaluateResponseBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public TaskToEvaluateResponseBuilder withActivityName(String activityName) {
            this.activityName = activityName;
            return this;
        }

        public TaskToEvaluateResponseBuilder withIsLate(Boolean isLate) {
            this.isLate = isLate;
            return this;
        }

        public TaskToEvaluateResponseBuilder withActivityDetails(String activityDetails) {
            this.activityDetails = activityDetails;
            return this;
        }

        public TaskToEvaluateResponseBuilder withUserTitle(String userTitle) {
            this.userTitle = userTitle;
            return this;
        }

        public TaskToEvaluateResponseBuilder withUserContent(String userContent) {
            this.userContent = userContent;
            return this;
        }

        public TaskToEvaluateResponseBuilder withUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
            return this;
        }

        public TaskToEvaluateResponseBuilder withFile(List<FileResponse> file) {
            this.file = file;
            return this;
        }

        public TaskToEvaluateResponseBuilder withMaxPoints(Double maxPoints) {
            this.maxPoints = maxPoints;
            return this;
        }

        public TaskToEvaluateResponseBuilder withFileTaskId(Long fileTaskId) {
            this.fileTaskId = fileTaskId;
            return this;
        }

        public TaskToEvaluateResponseBuilder withActivtyId(Long activtyId) {
            this.activityId = activtyId;
            return this;
        }

        public TaskToEvaluateResponseBuilder withActivityType(ActivityType activityType) {
            this.activityType = activityType;
            return this;
        }

        public TaskToEvaluateResponseBuilder withRemaining(Long remaining) {
            this.remaining = remaining;
            return this;
        }

        public TaskToEvaluateResponse build() {
            return new TaskToEvaluateResponse(this.userEmail,
                    this.fileTaskResponseId,
                    this.responseId,
                    this.firstName,
                    this.lastName,
                    this.activityName,
                    this.isLate,
                    this.activityDetails,
                    Optional.ofNullable(this.userTitle),
                    Optional.ofNullable(this.userContent),
                    Optional.ofNullable(this.userAnswer),
                    Optional.ofNullable(this.file),
                    this.maxPoints,
                    this.fileTaskId,
                    this.activityId,
                    this.activityType,
                    this.remaining);
        }
    }
}
