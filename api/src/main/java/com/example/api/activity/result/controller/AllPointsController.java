package com.example.api.activity.result.controller;

import com.example.api.activity.task.dto.response.result.TotalPointsResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.service.AllPointsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points/all")
@SecurityRequirement(name = "JWT_AUTH")
public class AllPointsController {
    private final AllPointsService allPointsService;

    @GetMapping("/list/professor")
    public ResponseEntity<List<?>> getAllPointsListForProfessor(@RequestParam Long courseId, @RequestParam String studentEmail) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsListForProfessor(courseId, studentEmail));
    }

    @GetMapping("/list/student")
    public ResponseEntity<List<?>> getAllPointsListForStudent(@RequestParam Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsListForStudent(courseId));
    }

    @GetMapping("/total")
    public ResponseEntity<TotalPointsResponse> getAllPointsTotal(@RequestParam Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsTotal(courseId));
    }
}