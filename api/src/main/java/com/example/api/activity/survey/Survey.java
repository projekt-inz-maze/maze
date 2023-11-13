package com.example.api.activity.survey;

import com.example.api.activity.Activity;
import com.example.api.course.Course;
import com.example.api.activity.ActivityType;
import com.example.api.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Survey extends Activity {
    private final ActivityType activityType = ActivityType.SURVEY;
    private double points;

    public Survey(CreateSurveyForm form, User professor, Course course) {
        super(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), professor, course);
        this.points = form.getPoints();
    }

    @Override
    public Double getMaxPoints() {
        return points;
    }
}
