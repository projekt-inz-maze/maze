package com.example.api.validator;

import com.example.api.user.dto.request.RegisterUserForm;
import com.example.api.error.exception.*;
import com.example.api.group.model.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.user.model.hero.Hero;
import com.example.api.user.model.hero.UserHero;
import com.example.api.group.repository.GroupRepository;
import com.example.api.user.repository.HeroRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.user.service.util.ProfessorRegisterToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserValidator {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final HeroRepository heroRepository;
    private final ProfessorRegisterToken professorRegisterToken;
    private final String STUDENT_DOMAIN = "@student.agh.edu.pl";
    private final String PROFESSOR_DOMAIN = "@agh.edu.pl";

    public void validateUserIsNotNull(User user, String identifier) throws UsernameNotFoundException {
        if(user == null) {
            log.error("User {} not found in database", identifier);
            throw new UsernameNotFoundException("User " + identifier + " not found in database");
        }
    }

    public void validateUserIsNotNullWithMessage(User user, String email, String message) throws UsernameNotFoundException {
        if(user == null) {
            log.error("User {} not found in database", email);
            throw new UsernameNotFoundException(message);
        }
    }
    
    private void validateUserAccountType(User user, AccountType type) throws WrongUserTypeException {
        if(user.getAccountType() != type) {
            throw new WrongUserTypeException("Wrong user type!", type);
        }
    }

    public void validateStudentAccount(User student) throws UsernameNotFoundException, WrongUserTypeException {
        validateUserAccountType(student, AccountType.STUDENT);
    }

    public void validateStudentAccount(User student, Long id) throws UsernameNotFoundException, WrongUserTypeException {
        if(student == null) {
            log.error("User with id {} not found in database", id);
            throw new UsernameNotFoundException("User with id" + id + " not found in database");
        }
        validateUserAccountType(student, AccountType.STUDENT);
    }

    public void validateProfessorAccount(User professor) throws UsernameNotFoundException, WrongUserTypeException {
        validateUserAccountType(professor, AccountType.PROFESSOR);
    }

    public void validateUserDoesNotExist(String email) throws EntityAlreadyInDatabaseException {
        if (userRepository.existsByEmail(email)) {
            log.error("User {} already exist in database", email);
            throw new EntityAlreadyInDatabaseException(ExceptionMessage.EMAIL_TAKEN);
        }
    }

    public void validateEmail(String email, String domain) throws RequestValidationException {
        if (email.contains(";")) {
            log.error("Email cannot contain a semicolon!");
            throw new RequestValidationException(ExceptionMessage.EMAIL_CONTAINS_SEMICOLON);
        }

        if (!email.endsWith(domain)) {
            log.error("Email not from AGH domain");
            throw new RequestValidationException(ExceptionMessage.EMAIL_WRONG_DOMAIN);
        }
    }
    public void validateStudentEmail(String email) throws RequestValidationException {
        validateEmail(email, STUDENT_DOMAIN);
    }
    public void validateProfessorEmail(String email) throws RequestValidationException {
        validateEmail(email, PROFESSOR_DOMAIN);
    }



    public void validateStudentRegistrationForm(RegisterUserForm form) throws RequestValidationException {
        if (form.getHeroType() == null || form.getInvitationCode() == null) {
            log.error("Request body for registering student requires 6 body parameters");
            throw new WrongBodyParametersNumberException("Request body for registering student requires 6 body parameters",
                    List.of("firstName", "lastName", "email", "password", "heroType", "invitationCode"), 1);
        }
    }

    public void validateStudentWithIndexNumberDoesNotExist(Integer indexNumber) throws RequestValidationException {
        if(indexNumber == null || userRepository.existsUserByIndexNumber(indexNumber)) {
            log.error("User with index number {} already in database", indexNumber);
            throw new EntityAlreadyInDatabaseException(ExceptionMessage.INDEX_TAKEN);
        }
    }

    public void validateProfessorValidationForm(RegisterUserForm form) throws RequestValidationException {
        if (form.getHeroType() != null || form.getInvitationCode() != null || form.getIndexNumber() != null){
            log.error("Request body for registering professor requires 5 body parameters");
            throw new WrongBodyParametersNumberException("Request body for registering professor requires 5 body parameters",
                    List.of("firstName", "lastName", "email", "password", "professorRegisterToken"), 1);
        }

        if (form.getProfessorRegistrationToken() == null) {
            log.error("ProfessorRegisterToken not passed for professor registration");
            throw new RequestValidationException(ExceptionMessage.PROFESSOR_REGISTER_TOKEN_NOT_PASSED);
        }

        if (!professorRegisterToken.isValid(form.getProfessorRegistrationToken())) {
            log.error("Wrong ProfessorRegisterToken passed");
            throw new RequestValidationException(ExceptionMessage.WRONG_PROFESSOR_REGISTER_TOKEN);
        }
    }
}
