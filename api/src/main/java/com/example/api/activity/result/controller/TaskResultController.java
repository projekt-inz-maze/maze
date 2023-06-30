package com.example.api.activity.result.controller;

import com.example.api.activity.task.dto.request.GetCSVForm;
import com.example.api.activity.task.dto.response.result.ActivityStatisticsResponse;
import com.example.api.activity.task.dto.response.result.TaskPointsStatisticsResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.service.TaskResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/result")
@SecurityRequirement(name = "JWT_AUTH")
public class TaskResultController {
    private final TaskResultService resultService;

    @PostMapping("/csv")
    public ResponseEntity<ByteArrayResource> getCSVFile(@RequestBody GetCSVForm form) throws IOException {
        return ResponseEntity.ok().body(resultService.getCSVFile(form));
    }

    @GetMapping("/points/statistics")
    public ResponseEntity<List<TaskPointsStatisticsResponse>> getUserPointsStatistics() throws WrongUserTypeException {
        return ResponseEntity.ok().body(resultService.getUserPointsStatistics());
    }

    @GetMapping("/activity/statistics")
    public ResponseEntity<ActivityStatisticsResponse> getActivityStatistics(@RequestParam Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(resultService.getActivityStatistics(activityID));
    }
}
