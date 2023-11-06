package com.example.api.question.answer;

import com.example.api.question.Question;
import com.example.api.question.option.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    @Nullable
    @ManyToMany
    private List<Option> options;

    @Nullable
    private String openAnswer;
}
