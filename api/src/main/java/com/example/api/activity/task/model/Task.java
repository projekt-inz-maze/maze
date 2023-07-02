package com.example.api.activity.task.model;

import com.example.api.course.model.Course;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Task extends Activity {
    private String requiredKnowledge;
    private Double maxPoints;

    public Task(String name,
                String description,
                int posX,
                int posY,
                User professor,
                String requiredKnowledge,
                Double maxPoints,
                Course course){
        super(name, description, posX, posY, professor, course);
        this.requiredKnowledge = requiredKnowledge;
        this.maxPoints = maxPoints;
    }
}
