package com.example.api.chapter.map;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
@SecurityRequirement(name = "JWT_AUTH")
public class ActivityMapController {
    private final ActivityMapService activityMapService;

    @GetMapping
    public ResponseEntity<ActivityMapResponse> getActivityMap(@RequestParam Long activityMapId)
            throws EntityNotFoundException, WrongUserTypeException {
        return ResponseEntity.ok().body(activityMapService.getActivityMap(activityMapId));
    }
}
