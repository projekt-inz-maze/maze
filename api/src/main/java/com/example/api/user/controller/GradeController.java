package com.example.api.user.controller;

import com.example.api.user.dto.response.grade.GradeResponse;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.service.GradeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
@SecurityRequirement(name = "JWT_AUTH")
public class GradeController {
    private final GradeService gradeService;

    @GetMapping
    public ResponseEntity<List<GradeResponse>> getAllGrades() throws WrongUserTypeException {
        return ResponseEntity.ok().body(gradeService.getAllGrades());
    }
}
