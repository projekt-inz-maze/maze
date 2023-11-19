package com.example.api.activity.submittask.result;

import com.example.api.activity.result.model.ActivityResult;
import com.example.api.course.coursemember.CourseMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubmitTaskResult extends ActivityResult {
    private boolean isEvaluated;

    private String submittedTitle;
    private String submittedContent;

    public SubmitTaskResult(SubmitTaskResultDTO dto, CourseMember courseMember) {
        super(courseMember);
        this.submittedContent = dto.getContent();
        this.submittedTitle = dto.getTitle();
    }

    @Override
    public boolean isEvaluated() {
        return isEvaluated;
    }
}
