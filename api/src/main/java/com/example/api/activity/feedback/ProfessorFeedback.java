package com.example.api.activity.feedback;

import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.file.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProfessorFeedback extends Feedback {
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FileTaskResult fileTaskResult;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File feedbackFile;

    private Double points;
}
