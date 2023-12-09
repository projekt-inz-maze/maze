package com.example.api.course;

import com.example.api.error.exception.RequestValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
@SecurityRequirement(name = "JWT_AUTH")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Long> createCourse(@RequestBody SaveCourseForm form) throws RequestValidationException {
        return ResponseEntity.ok().body(courseService.saveCourse(form));
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getCoursesForUser() {
        return ResponseEntity.ok().body(courseService.getCoursesForUser());
    }

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCourse(@RequestParam Long courseId) throws RequestValidationException {
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit")
    ResponseEntity<?> editCourse(@RequestBody CourseDTO dto) throws RequestValidationException {
        return ResponseEntity.ok().body(courseService.editCourse(dto));
    }
}
