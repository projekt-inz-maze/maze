package com.example.api.activity.task.dto.response.result.question;

import com.example.api.question.Difficulty;
import com.example.api.question.Question;
import com.example.api.question.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionList {
    private Long id;
    private QuestionType type;
    private String hint;
    private Difficulty difficulty;
    private Double points;

    public QuestionList(Question question) {
        this.id = question.getId();
        this.type = question.getType();
        this.hint = question.getHint();
        this.difficulty = question.getDifficulty();
        this.points = question.getPoints();
    }
}
