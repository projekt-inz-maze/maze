package com.example.api.personality;


import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/personality")
@SecurityRequirement(name = "JWT_AUTH")
@Slf4j
public class PersonalityController {
    private final PersonalityService personalityService;
    private final UserService userService;
    @GetMapping("/quiz")
    ResponseEntity<Object> getQuiz() {
        Resource resource = new ClassPathResource("quiz.json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            return ResponseEntity.ok(mapper.readValue(resource.getInputStream(), Object.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/quiz")
    ResponseEntity<?> postQuiz(@RequestBody Map<PersonalityType, Integer> result) throws WrongUserTypeException {
        personalityService.mapToPersonalityType(result);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    ResponseEntity<String> getPersonality() throws WrongUserTypeException {
        var personality = userService.getCurrentUserAndValidateStudentAccount().getPersonality();
        return ResponseEntity.ok((new JSONObject(personality)).toString());
    }
}
