package com.example.api.user.service;

import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.coursemember.CourseMemberService;
import com.example.api.course.StudentNotEnrolledException;
import com.example.api.group.GroupService;
import com.example.api.personalityquiz.PersonalityType;
import com.example.api.user.hero.HeroType;
import com.example.api.user.hero.model.Hero;
import com.example.api.user.hero.model.UserHero;
import com.example.api.user.hero.HeroRepository;
import com.example.api.user.service.util.ProfessorRegisterToken;
import com.example.api.user.dto.request.EditPasswordForm;
import com.example.api.user.dto.request.RegisterUserForm;
import com.example.api.user.dto.request.SetStudentGroupForm;
import com.example.api.user.dto.request.SetStudentIndexForm;
import com.example.api.user.dto.response.BasicStudent;
import com.example.api.error.exception.*;
import com.example.api.group.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.activity.info.InfoRepository;
import com.example.api.activity.survey.SurveyRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.LoggedInUserService;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyRepository surveyRepository;
    private final InfoRepository infoRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final LoggedInUserService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final ProfessorRegisterToken professorRegisterToken;
    private final PasswordValidator passwordValidator;
    private final GroupService groupService;
    private final CourseMemberService courseMemberService;
    private final HeroRepository heroRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);
        log.info("User {} found in database", email);
        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getAccountType().getName()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public User getUser(Long userId) {
        User user = userRepository.findUserById(userId);
        userValidator.validateUserIsNotNull(user, userId.toString());
        return user;
    }

    public User saveUser(User user) {
        log.info("Saving user {} to the database", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Long registerUser(RegisterUserForm form) throws RequestValidationException {
        String email = form.getEmail();
        log.info("Registering user {}", email);
        userValidator.validateUserDoesNotExist(email);

        if (form.getAccountType().equals(AccountType.STUDENT)) {
            return registerStudent(form);
        } else if (form.getAccountType().equals(AccountType.PROFESSOR)) {
            return registerProfessor(form);
        } else {
            throw new RequestValidationException(ExceptionMessage.ROLE_NOT_ALLOWED);
        }
    }

    private Long registerProfessor(RegisterUserForm form) throws RequestValidationException {
        userValidator.validateProfessorEmail(form.getEmail());

        User user = new User(form.getEmail(), form.getFirstName(), form.getLastName(), form.getAccountType());
        userValidator.validateProfessorValidationForm(form);
        passwordValidator.validatePassword(form.getPassword());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        userRepository.save(user);
        return user.getId();
    }

    private Long registerStudent(RegisterUserForm form) throws RequestValidationException {
        userValidator.validateStudentEmail(form.getEmail());
        User user = new User(form.getEmail(), form.getFirstName(), form.getLastName(), form.getAccountType());
        userValidator.validateStudentRegistrationForm(form);

        Integer indexNumber = form.getIndexNumber();
        userValidator.validateStudentWithIndexNumberDoesNotExist(indexNumber);
        user.setIndexNumber(indexNumber);

        passwordValidator.validatePassword(form.getPassword());
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userRepository.save(user);

        addUserToGroup(user, form.getInvitationCode(), form.getHeroType());

        return user.getId();
    }

    public void editPassword(EditPasswordForm form){
        User user = authService.getCurrentUser();
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
    }

    public User getUser(String email) throws UsernameNotFoundException {
        log.info("Fetching user {}", email);
        User user = userRepository.findUserByEmail(email);
        userValidator.validateUserIsNotNull(user, email);
        return user;
    }

    public Group getCurrentUserGroup(Long courseId) throws StudentNotEnrolledException {
        User user = authService.getCurrentUser();
        log.info("Fetching group for user {}", user.getEmail());
        return user.getCourseMember(courseId).orElseThrow(() -> new StudentNotEnrolledException(user, courseId)).getGroup();
    }

    public List<BasicStudent> getAllStudentsWithGroup(Long courseId) {
        log.info("Fetching all students with group for course {}", courseId);

        return courseMemberService.getAll(courseId)
                .stream()
                .map(BasicStudent::new)
                .toList();
    }

    public Group updateStudentGroup(SetStudentGroupForm setStudentGroupForm)
            throws EntityNotFoundException, WrongUserTypeException {
        Long studentId = setStudentGroupForm.getStudentId();
        Long groupId = setStudentGroupForm.getNewGroupId();

        log.info("Adding student {} to group {}", studentId, groupId);
        User user = getUser(studentId);
        userValidator.validateStudentAccount(user, studentId);

        Group newGroup = groupService.getGroupById(groupId);
        return updateStudentGroup(user, newGroup);
    }

    public Group updateStudentGroup(User user, Group newGroup) {
        Optional<CourseMember> courseMember = user.getCourseMember(newGroup.getCourse().getId());
            courseMemberService.updateGroup(courseMember.orElseThrow(), newGroup);
        return newGroup;
    }

    public void addUserToGroup(String invitationCode, HeroType heroType) throws WrongUserTypeException, EntityNotFoundException {
        User student = authService.getCurrentUser();
        userValidator.validateStudentAccount(student);
        addUserToGroup(student, invitationCode, heroType);
    }

    private void addUserToGroup(User user, String invitationCode, HeroType heroType) throws EntityNotFoundException {
        Group group = groupService.getGroupByInvitationCode(invitationCode);
        userValidator.validateUserNotInCourse(user, group.getCourse());

        Hero hero = heroRepository.findHeroByTypeAndCourse(heroType, group.getCourse());
        UserHero userHero = new UserHero(hero, 0, 0L, group.getCourse());
        CourseMember courseMember = courseMemberService.create(user, group, userHero);
        user.getCourseMemberships().add(courseMember);
        groupService.addUser(courseMember, group);
        userRepository.save(user);
    }

    public Integer setIndexNumber(SetStudentIndexForm setStudentIndexForm) throws WrongUserTypeException, EntityAlreadyInDatabaseException {
        User student = authService.getCurrentUser();
        String email = student.getEmail();
        log.info("Setting index number {} for user with email {}", setStudentIndexForm.getNewIndexNumber(), email);
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
        User user = authService.getCurrentUser();
        userValidator.validateProfessorAccount(user);

        log.info("Professor {} fetch ProfessorRegisterToken", user.getEmail());
        return professorRegisterToken.getToken();
    }

    public void deleteProfessorAccount(String professorEmail) throws WrongUserTypeException {
        User professor = authService.getCurrentUser();
        User newProfessor = userRepository.findUserByEmail(professorEmail);
        userValidator.validateProfessorAccount(professor);
        userValidator.validateProfessorAccount(newProfessor);

        changeUserForActivitiesAndAdditionalPoints(professor, newProfessor);
        userRepository.delete(professor);
    }

    public void deleteStudentAccount() throws WrongUserTypeException {
        User user = authService.getCurrentUser();
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
        User user = authService.getCurrentUser();
        String professorEmail = user.getEmail();
        return userRepository.findAllByAccountTypeEquals(AccountType.PROFESSOR)
                .stream()
                .map(User::getEmail)
                .filter(email -> !email.equals(professorEmail))
                .toList();
    }

    public User getCurrentUserAndValidateStudentAccount() throws WrongUserTypeException {
        User user = authService.getCurrentUser();
        userValidator.validateStudentAccount(user);
        return user;
    }
    public User getCurrentUserAndValidateProfessorAccount() throws WrongUserTypeException {
        User user = authService.getCurrentUser();
        userValidator.validateProfessorAccount(user);
        return user;
    }

    public void setPersonalityForUser(User user, Map<PersonalityType, Integer> quizResult) {
        int allAnswers = quizResult.values().stream().reduce(Integer::sum).orElseThrow();

        Map<PersonalityType, Double> personality = quizResult
                .entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), 100D * (double)e.getValue()/(double) allAnswers))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        user.setPersonality(personality);
        userRepository.save(user);
    }
}
