package com.example.api.activity.survey;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.util.message.MessageManager;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
@SecurityRequirement(name = "JWT_AUTH")
public class SurveyController {
    private final SurveyService surveyService;

    @GetMapping
    ResponseEntity<SurveyInfoResponse> getSurveyInfo(@RequestParam Long surveyId)
            throws EntityNotFoundException, WrongUserTypeException {
        return ResponseEntity.ok().body(surveyService.getSurveyInfo(surveyId));
    }

    @GetMapping("/create")
    ResponseEntity<CreateSurveyForm> getExampleCreateSurveyForm() {
        CreateSurveyForm form = new CreateSurveyForm(
                MessageManager.TITLE,
                MessageManager.DESC,
                4,
                5,
                10.0
        );
        return ResponseEntity.ok().body(form);
    }

    @PostMapping("/create")
    ResponseEntity<?> createSurvey(@RequestBody CreateSurveyChapterForm form)
            throws RequestValidationException {
        surveyService.createSurvey(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
