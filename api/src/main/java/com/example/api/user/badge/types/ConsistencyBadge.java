package com.example.api.user.badge.types;

import com.example.api.course.Course;
import com.example.api.user.badge.dtos.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.util.model.Image;
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
public class ConsistencyBadge extends Badge{
    private Integer weeksInRow;

    public ConsistencyBadge(Long id, String title, String description, Image image, Integer weeksInRow, Course course) {
        super(id, title, description, image, course);
        this.weeksInRow = weeksInRow;
    }

    @Override
    public boolean isGranted(BadgeVisitor visitor) {
        return visitor.visitConsistencyBadge(this);
    }

    @Override
    public BadgeResponse<?> getResponse() {
        BadgeResponse<Integer> response = new BadgeResponse<>(this);
        response.setValue(weeksInRow);
        return response;
    }

    public void update(BadgeUpdateForm form, BadgeValidator validator) throws IOException, RequestValidationException {
        super.update(form, validator);
        this.weeksInRow = validator.validateAndGetIntegerValue(form.getValue());
    }
}
