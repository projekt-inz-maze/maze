package com.example.api.activity.task.dto.response.result.summary.util;

import com.example.api.activity.task.dto.response.result.summary.Score;
import com.example.api.activity.result.model.TaskResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ScoreCreator {
    private String groupName;
    private Double maxPoints;
    private Double sumOfScores;
    private Integer numberOfScores;

    public ScoreCreator(String groupName, Double maxPoints) {
        this.groupName = groupName;
        this.maxPoints = maxPoints;
        this.sumOfScores = 0D;
        this.numberOfScores = 0;
    }

    public void add(TaskResult taskResult) {
            sumOfScores += taskResult.getPoints();
            numberOfScores += 1;
    }

    public Optional<Score> create() {
        if (numberOfScores == 0) {
            return Optional.empty();
        }

        Double score = 100 * sumOfScores / (maxPoints * numberOfScores);
        score = Math.round(score * 10.0) / 10.0;
        return Optional.of(new Score(groupName, score));
    }
}
