package com.example.api.group.service;

import com.example.api.group.dto.request.SaveGroupForm;
import com.example.api.group.dto.response.GroupCode;
import com.example.api.user.dto.response.BasicUser;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.group.model.Group;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.group.repository.GroupRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.GroupValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupService {
    private final AuthenticationService authService;
    private final GroupRepository groupRepository;
    private final GroupValidator groupValidator;
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public Group saveGroup(Group group) {
        log.info("Saving group to database with name {}", group.getName());
        return groupRepository.save(group);
    }

    public Long saveGroup(SaveGroupForm form) throws RequestValidationException {
        log.info("Saving group to database with name {}", form.getName());
        List<Group> groups = groupRepository.findAll();
        groupValidator.validateGroup(groups, form);
        Group group = new Group(null, form.getName(), new ArrayList<>(), form.getInvitationCode());
        groupRepository.save(group);
        return group.getId();
    }

    public Group getGroupById(Long id) throws EntityNotFoundException {
        log.info("Fetching group with id {}", id);
        Group group = groupRepository.findGroupById(id);
        groupValidator.validateGroupIsNotNull(group, id);
        return group;
    }

    public Group getGroupByInvitationCode(String code) throws EntityNotFoundException {
        log.info("Fetching group with code {}", code);
        Group group = groupRepository.findGroupByInvitationCode(code);
        groupValidator.validateGroupIsNotNull(group, code);
        return group;
    }

    public List<GroupCode> getInvitationCodeList() throws WrongUserTypeException {
        log.info("Fetching group code list");
        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor, email);

        return groupRepository.findAll()
                .stream()
                .map(group -> new GroupCode(
                        group.getId(),
                        group.getName(),
                        group.getInvitationCode(),
                        group.getUsers()
                                .stream()
                                .filter(user -> user.getAccountType().equals(AccountType.STUDENT))
                                .count()
                        )
                )
                .toList();

    }

    public List<BasicUser> getGroupUserList(Long id) throws EntityNotFoundException {
        log.info("Fetching users from group with id {}", id);
        Group group = groupRepository.findGroupById(id);
        groupValidator.validateGroupIsNotNull(group, id);
        return group.getUsers()
                .stream()
                .map(BasicUser::new)
                .toList();

    }

    public List<BasicUser> getGroupStudentList(Long id) throws EntityNotFoundException {
        log.info("Fetching users from group with id {}", id);
        Group group = groupRepository.findGroupById(id);
        groupValidator.validateGroupIsNotNull(group, id);
        return group.getUsers()
                .stream()
                .filter(user -> user.getAccountType() == AccountType.STUDENT)
                .map(BasicUser::new)
                .toList();

    }

    public List<BasicUser> getGroupProfessorList(Long id) throws EntityNotFoundException {
        log.info("Fetching users from group with id {}", id);
        Group group = groupRepository.findGroupById(id);
        groupValidator.validateGroupIsNotNull(group, id);
        return group.getUsers()
                .stream()
                .filter(user -> user.getAccountType() == AccountType.PROFESSOR)
                .map(BasicUser::new)
                .toList();
    }

}
