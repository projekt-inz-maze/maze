package com.example.api.question.model;

import com.example.api.activity.task.dto.request.create.OptionForm;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question;

    public Option(OptionForm form, Question question) {
        this.content = form.getContent();
        this.isCorrect = form.getIsCorrect();
        this.question = question;
    }

    public Option(String content, boolean isCorrect, Question question) {
        this.content = content;
        this.isCorrect = isCorrect;
        this.question = question;
    }
}
