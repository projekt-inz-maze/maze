package com.example.api.activity.task;

import com.example.api.activity.task.dto.response.ActivitiesResponse;
import com.example.api.activity.task.dto.response.ActivityToEvaluateResponse;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.error.exception.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
@SecurityRequirement(name = "JWT_AUTH")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/evaluate/all")
    ResponseEntity<List<ActivityToEvaluateResponse>> getAllActivitiesToEvaluate(@RequestParam Long courseId)
            throws RequestValidationException {
        return ResponseEntity.ok().body(taskService.getAllActivitiesToEvaluate(courseId));
    }

    @GetMapping("/evaluate/first")
    ResponseEntity<TaskToEvaluateResponse> getFirstAnswerToEvaluate(@RequestParam Long fileTaskId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(taskService.getFirstAnswerToEvaluate(fileTaskId));
    }

    @GetMapping("/evaluate/support")
    ResponseEntity<List<TaskToEvaluateResponse>> getAllAnswerToEvaluate(@RequestParam Long courseId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(taskService.getAllAnswersToEvaluate(courseId));
    }

    @GetMapping("/activities")
    ResponseEntity<List<ActivitiesResponse>> getAllActivities(@RequestParam Long courseId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(taskService.getAllActivities(courseId));
    }

    @GetMapping("/requirements")
    ResponseEntity<RequirementResponse> getRequirementsForActivity(@RequestParam Long activityId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(taskService.getRequirementsForActivity(activityId));
    }

    @PostMapping("/requirements/update")
    ResponseEntity<?> updateRequirementForActivity(@RequestBody ActivityRequirementForm form) throws RequestValidationException {
        taskService.updateRequirementForActivity(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
