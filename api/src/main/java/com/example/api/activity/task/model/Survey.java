package com.example.api.activity.task.model;

import com.example.api.activity.task.dto.request.create.CreateSurveyForm;
import com.example.api.course.model.Course;
import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Survey extends Activity{
    private ActivityType activityType = ActivityType.SURVEY;
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
