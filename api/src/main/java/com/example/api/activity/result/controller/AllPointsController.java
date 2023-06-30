package com.example.api.activity.result.controller;

import com.example.api.activity.task.dto.response.result.TotalPointsResponse;
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
    public ResponseEntity<List<?>> getAllPointsListForProfessor(@RequestParam String studentEmail) throws WrongUserTypeException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsListForProfessor(studentEmail));
    }

    @GetMapping("/list/student")
    public ResponseEntity<List<?>> getAllPointsListForStudent() throws WrongUserTypeException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsListForStudent());
    }

    @GetMapping("/total")
    public ResponseEntity<TotalPointsResponse> getAllPointsTotal() throws WrongUserTypeException {
        return ResponseEntity.ok().body(allPointsService.getAllPointsTotal());
    }
}