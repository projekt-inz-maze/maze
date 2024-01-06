package com.example.api.group;

import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.CourseService;
import com.example.api.user.dto.response.BasicUser;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.security.LoggedInUserService;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupService {
    private final LoggedInUserService authService;
    private final GroupRepository groupRepository;
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;
    private final CourseService courseService;

    public Group saveGroup(Group group) {
        log.info("Saving group to database with name {}", group.getName());
        return groupRepository.save(group);
    }

    public Long createGroup(SaveGroupForm form) throws RequestValidationException {
        log.info("Saving group to database with name {}", form.getName());
        List<Group> groups = groupRepository.findAll();
        groupValidator.validateGroup(groups, form);
        Course course = courseService.getCourse(form.getCourseId());
        Group group = new Group(null, form.getName(), form.getInvitationCode(), course);
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

    public List<GroupCode> getInvitationCodeList(Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        log.info("Fetching group code list");
        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);
        Course course = courseService.getCourse(courseId);

        return groupRepository.findAllByCourse(course)
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

    public void removeUser(CourseMember courseMember, Group group) {
        group.getUsers().remove(courseMember.getUser());
        group.getMembers().remove(courseMember);
        groupRepository.save(group);
    }

    public void addUser(CourseMember courseMember, Group group) {
        group.getMembers().add(courseMember);
        group.getUsers().add(courseMember.getUser());
        groupRepository.save(group);
    }
}
