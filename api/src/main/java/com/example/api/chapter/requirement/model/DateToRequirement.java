package com.example.api.chapter.requirement.model;

import com.example.api.course.model.Course;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.util.visitor.RequirementFulfilledVisitor;
import com.example.api.util.visitor.RequirementValueVisitor;
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
public class DateToRequirement extends Requirement {
    private Long dateToMillis;

    public DateToRequirement(String name, Boolean isSelected, Long dateToMillis) {
        super(name, isSelected);
        this.dateToMillis = dateToMillis;
    }

    @Override
    public Long getDateToMillis() {
        return dateToMillis;
    }

    @Override
    public boolean isFulfilled(RequirementFulfilledVisitor visitor, Course course) {
        return visitor.visitDateToRequirement(this);
    }

    @Override
    public void setValue(RequirementValueVisitor visitor, String value) throws RequestValidationException {
        visitor.visitDateToRequirement(this, value);
    }

    @Override
    public RequirementDTO<Long> getResponse() {
        return new RequirementDTO<>(
                getId(),
                getName(),
                dateToMillis,
                RequirementValueType.DATE,
                getSelected()
        );
    }
}
