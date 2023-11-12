package com.example.api.util.csv;

import com.example.api.activity.feedback.Feedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.TaskResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CSVTaskResult {
    private Double points;
    private String info;

    public CSVTaskResult(TaskResult result) {
        this.info = "-";
        this.points = result == null ? null : result.getPoints();
    }

    public CSVTaskResult(FileTaskResult result, Feedback feedback) {
        if (feedback == null) {
            this.points = null;
            this.info = "-";
        } else {
            this.points = result.getPoints();
            this.info = feedback.getContent();
        }
    }

    public List<String> toStringList() {
        return points == null ? List.of("-", info) : List.of(points.toString(), info);
    }
}
