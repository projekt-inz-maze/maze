package com.example.api.course.controller;

import com.example.api.course.dto.request.SaveCourseForm;
import com.example.api.course.dto.response.CourseDTO;
import com.example.api.course.service.CourseService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.group.dto.request.SaveGroupForm;
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
    public ResponseEntity<Long> saveCourse(@RequestBody SaveCourseForm form) throws RequestValidationException {
        return ResponseEntity.ok().body(courseService.saveCourse(form));
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getCourserForUser() {
        return ResponseEntity.ok().body(courseService.getCoursesForUser());
    }

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCourse(@RequestParam Long courseId) throws RequestValidationException {
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    //TODO edit
}
