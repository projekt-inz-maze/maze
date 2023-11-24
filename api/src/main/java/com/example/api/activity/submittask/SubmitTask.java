package com.example.api.activity.submittask;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityType;
import com.example.api.course.Course;
import com.example.api.user.model.User;
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
public class SubmitTask extends Activity {
    private ActivityType activityType = ActivityType.SUBMIT;
    private int percentageForAuthor;

    public SubmitTask(CreateSubmitTaskForm form, User professor, Course course) {
        super(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), professor, course, form.getMaxPointsForAuthor());
        this.percentageForAuthor = form.getPercentageForAuthor();
    }
}
