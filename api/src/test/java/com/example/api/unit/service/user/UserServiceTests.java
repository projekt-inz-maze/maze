package com.example.api.unit.service.user;

import com.example.api.user.dto.request.RegisterUserForm;
import com.example.api.user.dto.request.SetStudentGroupForm;
import com.example.api.user.dto.response.BasicStudent;
import com.example.api.error.exception.*;
import com.example.api.group.model.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.User;
import com.example.api.activity.repository.result.ProfessorFeedbackRepository;
import com.example.api.activity.repository.task.FileTaskRepository;
import com.example.api.activity.repository.task.GraphTaskRepository;
import com.example.api.activity.repository.task.InfoRepository;
import com.example.api.activity.repository.task.SurveyRepository;
import com.example.api.group.repository.GroupRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.user.service.UserService;
import com.example.api.user.service.util.ProfessorRegisterToken;
import com.example.api.validator.PasswordValidator;
import com.example.api.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class UserServiceTests {
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationService authService;
    @Mock private Authentication authentication;
    @Mock private UserValidator userValidator;
    @Mock private GraphTaskRepository graphTaskRepository;
    @Mock private FileTaskRepository fileTaskRepository;
    @Mock private SurveyRepository surveyRepository;
    @Mock private InfoRepository infoRepository;
    @Mock private ProfessorFeedbackRepository professorFeedbackRepository;
    @Mock private ProfessorRegisterToken professorRegisterToken;
    @Mock private PasswordValidator passwordValidator;
    @Captor private ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    @Captor private ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
    @Captor private ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
    @Captor private ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    @Captor private ArgumentCaptor<Group> groupArgumentCaptor = ArgumentCaptor.forClass(Group.class);
    @Captor private ArgumentCaptor<AccountType> accountTypeArgumentCaptor = ArgumentCaptor.forClass(AccountType.class);

    User user;
    RegisterUserForm registerUserForm;
    Group group;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository,
                groupRepository,
                graphTaskRepository,
                fileTaskRepository,
                surveyRepository,
                infoRepository,
                professorFeedbackRepository,
                authService,
                passwordEncoder,
                userValidator,
                professorRegisterToken,
                passwordValidator
        );
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setPassword("password");

        group = new Group();
        group.setId(1L);
        group.setName("group");
        group.setInvitationCode("invitation-code");

        registerUserForm = new RegisterUserForm();
    }

    @Test
    public void loadUserByUsername() {
        // given
        user.setAccountType(AccountType.STUDENT);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);

        // when
        userService.loadUserByUsername(user.getEmail());

        // then
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        String capturedEmail = stringArgumentCaptor.getValue();
        assertThat(capturedEmail).isEqualTo(user.getEmail());
    }

    @Test
    public void saveUser() {
        // given
        String password = user.getPassword();
        String encodedPassword = "encodedPassword";
        given(userRepository.save(user)).willReturn(user);
        given(passwordEncoder.encode(user.getPassword())).willReturn(encodedPassword);

        // when
        userService.saveUser(user);

        // then
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(passwordEncoder).encode(stringArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        String capturedPassword = stringArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedPassword).isEqualTo(password);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    public void registerUserStudent() throws RequestValidationException {
        // given
        registerUserForm.setEmail(user.getEmail());
        registerUserForm.setInvitationCode(group.getInvitationCode());
        registerUserForm.setAccountType(AccountType.STUDENT);
        registerUserForm.setHeroType(HeroType.PRIEST);
        registerUserForm.setIndexNumber(99);
        String encodedPassword = "encodedPassword";
        given(userRepository.findUserByEmail(registerUserForm.getEmail())).willReturn(null);
        given(groupRepository.findGroupByInvitationCode(registerUserForm.getInvitationCode())).willReturn(group);
        given(userRepository.existsUserByIndexNumber(registerUserForm.getIndexNumber())).willReturn(false);
        given(passwordEncoder.encode(user.getPassword())).willReturn(encodedPassword);

        // when
        userService.registerUser(registerUserForm);

        // then
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        verify(passwordEncoder).encode(stringArgumentCaptor.capture());
        String capturedEmail = stringArgumentCaptor.getAllValues().get(0);
        String capturedPassword = stringArgumentCaptor.getAllValues().get(1);
        assertThat(capturedEmail).isEqualTo(registerUserForm.getEmail());
        assertThat(capturedPassword).isEqualTo(registerUserForm.getPassword());
    }

    @Test
    public void registerUserProfessor() throws RequestValidationException {
        // given
        registerUserForm.setEmail(user.getEmail());
        registerUserForm.setAccountType(AccountType.PROFESSOR);
        String encodedPassword = "encodedPassword";
        given(userRepository.findUserByEmail(registerUserForm.getEmail())).willReturn(null);
        given(passwordEncoder.encode(user.getPassword())).willReturn(encodedPassword);

        // when
        userService.registerUser(registerUserForm);

        // then
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        verify(passwordEncoder).encode(stringArgumentCaptor.capture());
        String capturedEmail = stringArgumentCaptor.getAllValues().get(0);
        String capturedPassword = stringArgumentCaptor.getAllValues().get(1);
        assertThat(capturedEmail).isEqualTo(registerUserForm.getEmail());
        assertThat(capturedPassword).isEqualTo(registerUserForm.getPassword());
    }

    @Test
    public void getUser() {
        // given
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);

        // when
        User returnedUser = userService.getUser(user.getEmail());

        // then
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        String capturedEmail = stringArgumentCaptor.getValue();
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void getUsers() {
        // given
        User secondUser = new User();
        secondUser.setId(2L);
        given(userRepository.findAll()).willReturn(List.of(user, secondUser));

        // when
        List<User> returnedUsers = userService.getUsers();

        // then
        verify(userRepository).findAll();
        assertThat(returnedUsers.size()).isEqualTo(2);
        assertThat(returnedUsers.contains(user)).isTrue();
        assertThat(returnedUsers.contains(secondUser)).isTrue();
    }

    @Test
    public void getUsersWhenIsEmpty() {
        // given
        given(userRepository.findAll()).willReturn(List.of());

        // when
        List<User> returnedUsers = userService.getUsers();

        // then
        verify(userRepository).findAll();
        assertThat(returnedUsers.size()).isEqualTo(0);
    }

    @Test
    public void getUserGroup() throws EntityNotFoundException {
        // given
        user.setGroup(group);
        given(userRepository.findUserByEmail(user.getEmail())).willReturn(user);
        given(authService.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(user.getEmail());

        // when
        Group userGroup = userService.getUserGroup();

        // then
        verify(userRepository).findUserByEmail(stringArgumentCaptor.capture());
        String capturedEmail = stringArgumentCaptor.getValue();
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(userGroup).isEqualTo(group);
    }

    @Test
    public void getAllStudentsWithGroup() {
        // given
        User secondUser = new User();
        Group secondGroup = new Group();
        user.setGroup(group);
        secondUser.setGroup(secondGroup);
        given(userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)).willReturn(List.of(user, secondUser));

        // when
        List<BasicStudent> studentsWithGroup = userService.getAllStudentsWithGroup();

        // then
        verify(userRepository).findAllByAccountTypeEquals(accountTypeArgumentCaptor.capture());
        AccountType capturedAccountType = accountTypeArgumentCaptor.getValue();
        assertThat(capturedAccountType).isEqualTo(AccountType.STUDENT);
        assertThat(studentsWithGroup.contains(new BasicStudent(user))).isTrue();
        assertThat(studentsWithGroup.contains(new BasicStudent(secondUser))).isTrue();
        assertThat(studentsWithGroup.size()).isEqualTo(2);
    }

    @Test
    public void getAllStudentsWithGroupWhenIsEmpty() {
        // given
        given(userRepository.findAllByAccountTypeEquals(AccountType.STUDENT)).willReturn(List.of());

        // when
        List<BasicStudent> studentsWithGroup = userService.getAllStudentsWithGroup();

        // then
        verify(userRepository).findAllByAccountTypeEquals(accountTypeArgumentCaptor.capture());
        AccountType capturedAccountType = accountTypeArgumentCaptor.getValue();
        assertThat(capturedAccountType).isEqualTo(AccountType.STUDENT);
        assertThat(studentsWithGroup.size()).isEqualTo(0);
    }

    @Test
    public void setStudentGroup() throws WrongUserTypeException, StudentAlreadyAssignedToGroupException, EntityNotFoundException {
        // given
        user.setAccountType(AccountType.STUDENT);;
        List<User> oldGroupUsers = new ArrayList<>();
        oldGroupUsers.add(user);
        group.setUsers(oldGroupUsers);
        user.setGroup(group);
        Group newGroup = new Group();
        newGroup.setId(2L);
        newGroup.setUsers(new ArrayList<>());
        SetStudentGroupForm setStudentGroupForm = new SetStudentGroupForm();
        setStudentGroupForm.setStudentId(user.getId());
        setStudentGroupForm.setNewGroupId(newGroup.getId());
        given(userRepository.findUserById(user.getId())).willReturn(user);
        given(groupRepository.findGroupById(group.getId())).willReturn(group);
        given(groupRepository.findGroupById(newGroup.getId())).willReturn(newGroup);

        //when
        userService.setStudentGroup(setStudentGroupForm);

        // then
        verify(userRepository).findUserById(idArgumentCaptor.capture());
        verify(groupRepository).findGroupById(idArgumentCaptor.capture());
        Long capturedUserId = idArgumentCaptor.getAllValues().get(0);
        Long capturedNewGroupId = idArgumentCaptor.getAllValues().get(1);
        assertThat(capturedUserId).isEqualTo(user.getId());
        assertThat(capturedNewGroupId).isEqualTo(newGroup.getId());
    }
}