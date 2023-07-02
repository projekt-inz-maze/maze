package com.example.api.course.controller;

import com.example.api.course.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
@SecurityRequirement(name = "JWT_AUTH")
public class CourseController {
    private final CourseService courseService;
}
