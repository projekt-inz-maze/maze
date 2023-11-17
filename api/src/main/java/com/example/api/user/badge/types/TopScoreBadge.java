package com.example.api.user.badge.types;

import com.example.api.course.Course;
import com.example.api.user.badge.dtos.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.user.dto.response.badge.BadgeResponseTopScore;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
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
public class TopScoreBadge extends Badge{
    private Double topScore;
    private Boolean forGroup;

    public TopScoreBadge(Long id, String title, String description, Image image, Double topScore, Boolean forGroup, Course course) {
        super(id, title, description, image, course);
        this.topScore = topScore;
        this.forGroup = forGroup;
    }

    @Override
    public boolean isGranted(BadgeVisitor visitor) throws WrongUserTypeException, EntityNotFoundException {
        return visitor.visitTopScoreBadge(this);
    }

    @Override
    public BadgeResponse<?> getResponse() {
        return new BadgeResponseTopScore(this);
    }

    public void update(BadgeUpdateForm form, BadgeValidator validator) throws IOException, RequestValidationException {
        super.update(form, validator);
        this.topScore = validator.validateAndGetDoubleValue(form.getValue());

        Boolean forGroup = form.getForValue();
        this.forGroup = Objects.requireNonNullElse(forGroup, false);
    }
}
