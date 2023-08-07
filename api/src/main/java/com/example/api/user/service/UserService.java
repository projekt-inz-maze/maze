package com.example.api.user.service;

import com.example.api.user.service.util.ProfessorRegisterToken;
import com.example.api.user.dto.request.EditPasswordForm;
import com.example.api.user.dto.request.RegisterUserForm;
import com.example.api.user.dto.request.SetStudentGroupForm;
import com.example.api.user.dto.request.SetStudentIndexForm;
import com.example.api.user.dto.response.BasicStudent;
import com.example.api.error.exception.*;
import com.example.api.group.model.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.activity.task.repository.GraphTaskRepository;
import com.example.api.activity.task.repository.InfoRepository;
import com.example.api.activity.task.repository.SurveyRepository;
import com.example.api.group.repository.GroupRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.PasswordValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyRepository surveyRepository;
    private final InfoRepository infoRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final AuthenticationService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final ProfessorRegisterToken professorRegisterToken;
    private final PasswordValidator passwordValidator;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);
        log.info("User {} found in database", email);
        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getAccountType().getName()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public User saveUser(User user) {
        log.info("Saving user {} to the database", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Long registerUser(RegisterUserForm form) throws RequestValidationException {
        String email = form.getEmail();
        log.info("Registering user {}", email);
        User dbUser = userRepository.findUserByEmail(email);
        User user = new User(form.getEmail(), form.getFirstName(), form.getLastName(), form.getAccountType());
        userValidator.validateUserRegistration(dbUser, user, form, email);
        passwordValidator.validatePassword(form.getPassword());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setPoints(0D);
        user.setLevel(1);
        userRepository.save(user);
        return user.getId();
    }

    public void editPassword(EditPasswordForm form){
        String email = authService.getAuthentication().getName();
        User user = getUser(email);
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
    }

    public User getUser(String email) throws UsernameNotFoundException {
        log.info("Fetching user {}", email);
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);
        return user;
    }

    public User getCurrentUser() throws UsernameNotFoundException {
        String email = authService.getAuthentication().getName();
        User user = getUser(email);
        userValidator.validateUserIsNotNull(user, email);
        return user;
    }

    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public Group getUserGroup(Long courseId) {
        User user = getCurrentUser();
        log.info("Fetching group for user {}", user.getEmail());
        return user.getGroup();
    }

    public List<BasicStudent> getAllStudentsWithGroup() {
        log.info("Fetching all students with group");
        List<User> students = userRepository.findAllByAccountTypeEquals(AccountType.STUDENT);
        return students.stream()
                .map(BasicStudent::new)
                .collect(Collectors.toList());
    }

    public Group setStudentGroup(SetStudentGroupForm setStudentGroupForm)
            throws EntityNotFoundException, WrongUserTypeException, StudentAlreadyAssignedToGroupException {
        Long studentId = setStudentGroupForm.getStudentId();
        Long newGroupId = setStudentGroupForm.getNewGroupId();
        log.info("Setting group for student with id {}", studentId);
        User user = userRepository.findUserById(studentId);
        userValidator.validateStudentAccount(user, studentId);
        Group newGroup = groupRepository.findGroupById(newGroupId);
        Group previousGroup = user.getGroup();
        userValidator.validateAndSetUserGroup(newGroup, previousGroup, newGroupId, user);
        return newGroup;
    }

    public Integer setIndexNumber(SetStudentIndexForm setStudentIndexForm) throws WrongUserTypeException, EntityAlreadyInDatabaseException {
        String email = authService.getAuthentication().getName();
        log.info("Setting index number {} for user with email {}", setStudentIndexForm.getNewIndexNumber(), email);
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student);

        if (student.getIndexNumber().equals(setStudentIndexForm.getNewIndexNumber())) {
            log.info("Student with email {} set again index number to {}", email, setStudentIndexForm.getNewIndexNumber());
            return student.getIndexNumber();
        }

        if (userRepository.existsUserByIndexNumber(setStudentIndexForm.getNewIndexNumber())) {
            log.error("Cannot set index number student with email {} to {} because it is taken", email, setStudentIndexForm.getNewIndexNumber());
            throw new EntityAlreadyInDatabaseException("Cannot set index number for user with email " + email + " to " + setStudentIndexForm.getNewIndexNumber() + " because is taken");
        }
        student.setIndexNumber(setStudentIndexForm.getNewIndexNumber());
        userRepository.save(student);
        return student.getIndexNumber();
    }

    public String getProfessorRegisterToken() throws WrongUserTypeException {
        User user = getCurrentUser();
        userValidator.validateProfessorAccount(user);

        log.info("Professor {} fetch ProfessorRegisterToken", user.getEmail());
        return professorRegisterToken.getToken();
    }

    public void deleteProfessorAccount(String professorEmail) throws WrongUserTypeException {
        User professor = getCurrentUser();
        User newProfessor = userRepository.findUserByEmail(professorEmail);
        userValidator.validateProfessorAccount(professor);
        userValidator.validateProfessorAccount(newProfessor);

        changeUserForActivitiesAndAdditionalPoints(professor, newProfessor);
        userRepository.delete(professor);
    }

    public void deleteStudentAccount() throws WrongUserTypeException {
        User user = getCurrentUser();
        userValidator.validateStudentAccount(user);
        userRepository.delete(user);
    }

    private void changeUserForActivitiesAndAdditionalPoints(User from, User to){
        Stream.of(graphTaskRepository.findAll(),
                        fileTaskRepository.findAll(),
                        surveyRepository.findAll(),
                        infoRepository.findAll())
                .flatMap(Collection::stream)
                .filter(activity -> activity.getProfessor() == from)
                .forEach(activity -> activity.setProfessor(to));
        additionalPointsRepository.findAll()
                .stream()
                .filter(additionalPoint -> additionalPoint.getProfessorEmail().equals(from.getEmail()))
                .forEach(additionalPoint -> additionalPoint.setProfessorEmail(to.getEmail()));
    }

    public List<String> getAllProfessorEmails() {
        User user = getCurrentUser();
        String professorEmail = user.getEmail();
        return userRepository.findAllByAccountTypeEquals(AccountType.PROFESSOR)
                .stream()
                .map(User::getEmail)
                .filter(email -> !email.equals(professorEmail))
                .toList();
    }

    public User getCurrentUserAndValidateStudentAccount() throws WrongUserTypeException {
        User user = getCurrentUser();
        userValidator.validateStudentAccount(user);
        return user;
    }
}
