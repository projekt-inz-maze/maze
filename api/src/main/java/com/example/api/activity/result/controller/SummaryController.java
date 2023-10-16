package com.example.api.activity.result.controller;

import com.example.api.activity.task.dto.response.result.summary.SummaryResponse;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.service.SummaryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/summary")
@SecurityRequirement(name = "JWT_AUTH")
public class SummaryController {
    private final SummaryService summaryService;
    @GetMapping("")
    public ResponseEntity<SummaryResponse> getUserPointsStatistics(@RequestParam Long courseId) throws RequestValidationException {
        return ResponseEntity.ok().body(summaryService.getSummary(courseId));
    }
}
