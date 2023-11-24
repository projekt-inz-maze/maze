package com.example.api.activity.task.filetask;

import com.example.api.activity.submittask.result.SubmitTaskResult;
import com.example.api.activity.task.Task;
import com.example.api.course.Course;
import com.example.api.activity.ActivityType;
import com.example.api.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FileTask extends Task {
    private ActivityType activityType = ActivityType.TASK;

    @OneToOne
    private SubmitTaskResult authoredByStudent;

    public FileTask(CreateFileTaskForm form, User professor, Course course) {
        super(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), professor,
                form.getRequiredKnowledge(), form.getMaxPoints(), course);
        double experience = form.getMaxPoints() * 10;
        super.setExperience(experience);
    }

    public Optional<SubmitTaskResult> getAuthoredByStudent() {
        return Optional.ofNullable(authoredByStudent);
    }
}
