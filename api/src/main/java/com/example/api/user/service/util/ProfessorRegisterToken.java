package com.example.api.user.service.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ProfessorRegisterToken {
    private String token;

    public boolean isValid(String givenToken) {
        return token.equals(givenToken);
    }
}
