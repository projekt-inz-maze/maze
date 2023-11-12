package com.example.api.chapter.requirement.model;

import com.example.api.course.Course;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.util.visitor.RequirementFulfilledVisitor;
import com.example.api.util.visitor.RequirementValueVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean selected;

    public Requirement(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public abstract boolean isFulfilled(RequirementFulfilledVisitor visitor, Course course);
    public abstract void setValue(RequirementValueVisitor visitor, String value) throws RequestValidationException;
    public abstract RequirementDTO<?> getResponse();
    public Long getDateToMillis() {
        return null;
    }
}
