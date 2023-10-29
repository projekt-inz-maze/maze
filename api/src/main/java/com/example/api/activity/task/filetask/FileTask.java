package com.example.api.activity.task.filetask;

import com.example.api.activity.task.Task;
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
public class FileTask extends Task {
    private ActivityType activityType = ActivityType.TASK;

    public FileTask(CreateFileTaskForm form, User professor, Course course) {
        super(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), professor,
                form.getRequiredKnowledge(), form.getMaxPoints(), course);
        double experiance = form.getMaxPoints() * 10;
        super.setExperience(experiance);
    }
}
