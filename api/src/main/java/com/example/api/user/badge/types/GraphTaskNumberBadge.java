package com.example.api.user.badge.types;

import com.example.api.course.Course;
import com.example.api.user.badge.dtos.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.file.image.Image;
import com.example.api.validator.BadgeValidator;
import com.example.api.user.badge.BadgeVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GraphTaskNumberBadge extends Badge{
    private Integer graphTaskNumber;

    public GraphTaskNumberBadge(Long id, String title, String description, Image image, Integer graphTaskNumber, Course course) {
        super(id, title, description, image, course);
        this.graphTaskNumber = graphTaskNumber;
    }

    @Override
    public boolean isGranted(BadgeVisitor visitor) {
        return visitor.visitGraphTaskNumberBadge(this);
    }

    @Override
    public BadgeResponse<?> getResponse() {
        BadgeResponse<Integer> response = new BadgeResponse<>(this);
        response.setValue(graphTaskNumber);
        return response;
    }

    public void update(BadgeUpdateForm form, BadgeValidator validator) throws IOException, RequestValidationException {
        super.update(form, validator);
        this.graphTaskNumber = validator.validateAndGetIntegerValue(form.getValue());
    }
}
