package com.example.api.user.badge.types;

import com.example.api.course.model.Course;
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
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ActivityScoreBadge extends Badge{
    private Double activityScore;
    private Boolean forOneActivity;

    public ActivityScoreBadge(Long id,
                              String title,
                              String description,
                              Image image, Double activityScore,
                              boolean forOneActivity,
                              Course course) {
        super(id, title, description, image, course);
        this.activityScore = activityScore;
        this.forOneActivity = forOneActivity;
    }

    @Override
    public boolean isGranted(BadgeVisitor visitor) {
        return visitor.visitActivityScoreBadge(this);
    }

    @Override
    public BadgeResponse<?> getResponse() {
        BadgeResponse<Double> response = new BadgeResponse<>(this);
        response.setValue(activityScore);
        return response;
    }

    public void update(BadgeUpdateForm form, BadgeValidator validator) throws IOException, RequestValidationException {
        super.update(form, validator);
        this.activityScore = validator.validateAndGetDoubleValue(form.getValue());
        Boolean forValue = form.getForValue();
        this.forOneActivity = Objects.requireNonNullElse(forValue, false);
    }
}
