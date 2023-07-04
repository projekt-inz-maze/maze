package com.example.api.group.controller;

import com.example.api.group.dto.request.SaveGroupForm;
import com.example.api.group.dto.response.GroupCode;
import com.example.api.user.dto.response.BasicUser;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.group.service.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
@SecurityRequirement(name = "JWT_AUTH")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Long> saveGroup(@RequestBody SaveGroupForm form) throws RequestValidationException {
        return ResponseEntity.ok().body(groupService.saveGroup(form));
    }

    @GetMapping("/invitation-code/list")
    ResponseEntity<List<GroupCode>> getInvitationCodeList(@RequestParam Long courseId) throws WrongUserTypeException {
        return ResponseEntity.ok().body(groupService.getInvitationCodeList());
    }

    @GetMapping("/users")
    public ResponseEntity<List<BasicUser>> getGroupUserList(@RequestParam Long groupId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(groupService.getGroupUserList(groupId));
    }

    @GetMapping("/students")
    public ResponseEntity<List<BasicUser>> getGroupStudentsList(@RequestParam Long groupId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(groupService.getGroupStudentList(groupId));
    }

    @GetMapping("/professors")
    public ResponseEntity<List<BasicUser>> getGroupProfessorList(@RequestParam Long groupId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(groupService.getGroupProfessorList(groupId));
    }
}
