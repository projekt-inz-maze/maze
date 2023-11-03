package com.example.api.question;


import com.example.api.course.model.Course;
import com.example.api.question.option.Option;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType type;
    private String content;
    private String hint;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "question")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private List<Option> options = new LinkedList<>();
    private Double points;

    @ManyToMany
    private List<Question> next = new LinkedList<>();

    @Nullable
    private String answerForOpenedQuestion;

    @ManyToOne
    private Course course;

    public Question(QuestionType type,
                    String content,
                    String hint,
                    Difficulty difficulty,
                    List<Option> options,
                    Double points,
                    List<Question> next,
                    @Nullable String answerForOpenedQuestion,
                    Course course) {
        this.type = type;
        this.content = content;
        this.hint = hint;
        this.difficulty = difficulty;
        this.options = options;
        this.points = points;
        this.next = next;
        this.answerForOpenedQuestion = answerForOpenedQuestion;
        this.course = course;
    }

    // Only for QuestionType.OPENED
    public Question(QuestionType type,
                    String content,
                    String hint,
                    Difficulty difficulty,
                    Double points,
                    String answerForOpenedQuestion) {
        this.type = type;
        this.content = content;
        this.hint = hint;
        this.difficulty = difficulty;
        this.points = points;
        this.answerForOpenedQuestion = answerForOpenedQuestion;
    }

    public Question(QuestionType type,
                    String content,
                    String hint,
                    Difficulty difficulty,
                    Double points) {
        this.type = type;
        this.content = content;
        this.hint = hint;
        this.difficulty = difficulty;
        this.points = points;
    }
}
