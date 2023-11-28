package com.example.api.personalityquiz;

import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.User;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonalityService {
    private final UserService userService;
    public void mapToPersonalityType(Map<PersonalityType, Integer> resultsMap) throws WrongUserTypeException {
        User student = userService.getCurrentUserAndValidateStudentAccount();
        userService.setPersonalityForUser(student, resultsMap);
    }
}
