package com.example.api.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.result.repository.ActivityResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class MapActivityConverter {
    private final RequirementService requirementService;
    private final ActivityResultRepository activityResultRepository;

    public MapActivityStudent toMapTaskStudent(Activity activity, User student) {
        MapActivityStudent.MapActivityStudentBuilder builder = MapActivityStudent.builder()
                .fromActivity(activity)
                .isFulfilled(areRequirementsFulfilled(activity))
                .requirements(mapRequirements(activity));

        Optional<ActivityResult> result = activityResultRepository.findByActivity_IdAndMember_User(activity.getId(), student);

        builder.awardedPoints(result.map(ActivityResult::getPoints).orElse(null));

        switch (activity.getActivityType()) {
            case EXPEDITION -> builder
                    .isCompleted(result.map(r -> r.getSendDateMillis() != null).orElse(false));
            case TASK, SURVEY, SUBMIT -> builder.isCompleted(result.isPresent());
            case INFO -> builder.isCompleted(true);
            case AUCTION -> builder.isCompleted(false);
            default -> throw new IllegalStateException("Invalid activity type");
        }

        return builder.build();
    }

    private Boolean areRequirementsFulfilled(Activity activity) {
        return requirementService.areRequirementsFulfilled(activity.getRequirements(), activity.getCourse());
    }

    private RequirementResponse mapRequirements(Activity activity) {
        List<? extends RequirementDTO<?>> requirements = activity.getRequirements()
                .stream()
                .map(Requirement::getResponse)
                .sorted(Comparator.comparingLong(RequirementDTO::getId))
                .toList();
        return new RequirementResponse(activity.getIsBlocked(), requirements);
    }
}
