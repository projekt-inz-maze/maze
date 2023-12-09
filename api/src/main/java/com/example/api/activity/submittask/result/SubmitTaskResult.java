package com.example.api.activity.submittask.result;

import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.submittask.SubmitTask;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.file.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubmitTaskResult extends ActivityResult {
    private boolean evaluated = false;

    private String submittedTitle;

    @Lob
    private String submittedContent;

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<File> files = new LinkedList<>();

    public SubmitTaskResult(SubmitTaskResultDTO dto, CourseMember courseMember, SubmitTask task) {
        super(courseMember, task);
        this.submittedContent = dto.getContent();
        this.submittedTitle = dto.getTitle();
    }

    public SubmitTaskResult(SubmitTaskResultWithFileDTO dto, CourseMember courseMember, SubmitTask task) {
        super(courseMember, task);
        this.submittedContent = dto.getContent();
        this.submittedTitle = dto.getTitle();
    }

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    public SubmitTask getSubmitTask() {
        return (SubmitTask) activity;
    }

    @Override
    public void setPoints(Double fullPoints) {
        Double pointPercentage = Math.round(fullPoints * getSubmitTask().getPercentageForAuthor()) / 100D;
        Double newPoints = Math.min( pointPercentage, getSubmitTask().getMaxPoints() - points);
        super.setPoints(newPoints);
    }
}
