package com.example.api.chapter.requirement.model;

import com.example.api.course.Course;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.user.model.User;
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
public class StudentsRequirement extends Requirement{
    @ManyToMany
    private List<User> allowedStudents = new LinkedList<>();

    public StudentsRequirement(String name, Boolean isSelected, List<User> allowedStudents) {
        super(name, isSelected);
        this.allowedStudents = allowedStudents;
    }

    @Override
    public boolean isFulfilled(RequirementFulfilledVisitor visitor, Course course) {
        return visitor.visitStudentsRequirements(this);
    }

    @Override
    public void setValue(RequirementValueVisitor visitor, String value) throws RequestValidationException {
        visitor.visitStudentsRequirements(this, value);
    }

    @Override
    public RequirementDTO<List<String>> getResponse() {
        List<String> emails = allowedStudents.stream()
                .map(User::getEmail)
                .toList();
        return new RequirementDTO<>(
                getId(),
                getName(),
                emails,
                RequirementValueType.MULTI_SELECT,
                getSelected()
        );
    }
}
