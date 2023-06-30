package com.example.api.map.model.requirement;

import com.example.api.map.dto.response.RequirementDTO;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.task.model.GraphTask;
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
    public boolean isFulfilled(RequirementFulfilledVisitor visitor) {
        return visitor.visitGraphTasksRequirement(this);
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
