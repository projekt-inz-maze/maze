package com.example.api.question.controller;

import com.example.api.activity.result.dto.request.QuestionActionForm;
import com.example.api.activity.task.dto.response.result.question.QuestionInfoResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.model.Question;
import com.example.api.question.service.QuestionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.TimeLimitExceededException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@SecurityRequirement(name = "JWT_AUTH")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<Question> getQuestion(@RequestParam Long questionId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(questionService.getQuestion(questionId));
    }

    @GetMapping("/get-info")
    public ResponseEntity<QuestionInfoResponse> getQuestionInfo(@RequestParam Long graphTaskId)
            throws RequestValidationException {
        return ResponseEntity.ok().body(questionService.getQuestionInfo(graphTaskId));
    }

    @PostMapping("/action")
    public ResponseEntity<Long> performQuestionAction(@RequestBody QuestionActionForm form)
            throws RequestValidationException, TimeLimitExceededException {
        return ResponseEntity.ok().body(questionService.performQuestionAction(form));
    }
}
