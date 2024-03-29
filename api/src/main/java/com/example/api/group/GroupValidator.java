package com.example.api.group;

import com.example.api.error.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GroupValidator {

    public void validateGroupIsNotNull(Group group, Long id) throws EntityNotFoundException {
        if (group == null) {
            log.error("Group with id {} not found in database", id);
            throw new EntityNotFoundException("Group with id" + id + " not found in database");
        }
    }

    public void validateGroupIsNotNull(Group group, String code) throws EntityNotFoundException {
        if (group == null) {
            log.error("Group with code {} not found in database", code);
            throw new EntityNotFoundException("Group with code" + code + " not found in database");
        }
    }

    public void validateGroupIsNotNullWithMessage(Group group, String groupName, String message) throws EntityNotFoundException {
        if (group == null) {
            log.error("Group with group name {} not found in database", groupName);
            throw new EntityNotFoundException(message);
        }
    }

    public void validateGroup(List<Group> groups, SaveGroupForm form) throws RequestValidationException {
        int idx = form.getName().indexOf(";");
        if (idx != -1) {
            log.error("Group name cannot contain a semicolon!");
            throw new RequestValidationException(ExceptionMessage.GROUP_NAME_CONTAINS_SEMICOLON);
        }
        boolean groupNameExists = groups.stream()
                .anyMatch(group -> group.getName().equals(form.getName()));
        if(groupNameExists) {
            log.error("Group with given name {} already exists", form.getName());
            throw new EntityAlreadyInDatabaseException(ExceptionMessage.GROUP_NAME_TAKEN);
        }
        boolean groupCodeExists = groups.stream()
                .anyMatch(group -> group.getInvitationCode().equals(form.getInvitationCode()));
        if(groupCodeExists) {
            log.error("Group with given code {} already exists", form.getInvitationCode());
            throw new EntityAlreadyInDatabaseException(ExceptionMessage.GROUP_CODE_TAKEN);
        }
    }
}
