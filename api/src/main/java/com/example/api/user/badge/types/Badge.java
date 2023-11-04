package com.example.api.user.badge.types;

import com.example.api.course.model.Course;
import com.example.api.user.badge.unlockedbadge.UnlockedBadge;
import com.example.api.user.badge.dtos.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.util.model.Image;
import com.example.api.validator.BadgeValidator;
import com.example.api.user.badge.BadgeVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Image image;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.REMOVE)
    private List<UnlockedBadge> unlockedBadges;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Badge(Long id, String title, String description, Image image, Course course) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.course = course;
    }

    public abstract boolean isGranted(BadgeVisitor visitor)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException;

    public abstract BadgeResponse<?> getResponse();

    public void update(BadgeUpdateForm form, BadgeValidator validator) throws IOException, RequestValidationException {
        validator.validateBadgeForm(form);
        this.title = form.getTitle();
        this.description = form.getDescription();

        MultipartFile image = form.getImage();
        if (image != null) {
            this.image.setFile(image.getBytes());
        }
    }
}
