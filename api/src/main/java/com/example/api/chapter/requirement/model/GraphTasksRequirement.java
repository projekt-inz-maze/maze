package com.example.api.chapter.requirement.model;

import com.example.api.course.model.Course;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.util.visitor.RequirementFulfilledVisitor;
import com.example.api.util.visitor.RequirementValueVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GraphTasksRequirement extends Requirement {
    @ManyToMany
    private List<GraphTask> finishedGraphTasks = new LinkedList<>();

    public GraphTasksRequirement(String name, Boolean isSelected, List<GraphTask> finishedGraphTasks) {
        super(name, isSelected);
        this.finishedGraphTasks = finishedGraphTasks;
    }

    @Override
    public boolean isFulfilled(RequirementFulfilledVisitor visitor, Course course) {
        return visitor.visitGraphTasksRequirement(this, course);
    }

    @Override
    public void setValue(RequirementValueVisitor visitor, String value) throws RequestValidationException {
        visitor.visitGraphTasksRequirement(this, value);
    }

    @Override
    public RequirementDTO<List<String>> getResponse() {
        List<String> titles = finishedGraphTasks.stream()
                .map(GraphTask::getTitle)
                .toList();
        return new RequirementDTO<>(
                getId(),
                getName(),
                titles,
                RequirementValueType.MULTI_SELECT,
                getSelected()
        );
    }
}
