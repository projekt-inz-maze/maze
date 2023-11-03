package com.example.api.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.dto.request.*;
import com.example.api.user.dto.response.BasicStudent;
import com.example.api.error.exception.*;
import com.example.api.group.Group;
import com.example.api.user.model.User;
import com.example.api.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT_AUTH")
public class UserController {
    private final UserService userService;
    private final LoggedInUserService authService;

    @PostMapping("/register")
    public ResponseEntity<Long> saveUser(@RequestBody RegisterUserForm form)
            throws RequestValidationException {
        return ResponseEntity.ok().body(userService.registerUser(form));
    }

    @PutMapping("/password-edition")
    public ResponseEntity<Long> editUserPassword(@RequestBody EditPasswordForm form){
        userService.editPassword(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/user/current")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok().body(authService.getCurrentUser());
    }

    @GetMapping("/user/group")
    public ResponseEntity<Group> getUserGroup(@RequestParam Long courseId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(userService.getCurrentUserGroup(courseId));
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws BadRequestHeadersException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refreshToken = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String email = decodedJWT.getSubject();
            User user = userService.getUser(email);
            String accessToken = JWT.create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                    .withIssuer(request.getRequestURI())
                    .withClaim("roles", List.of(user.getAccountType().getName()))
                    .sign(algorithm);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } else {
            throw new BadRequestHeadersException("Refresh token is missing or is invalid!");
        }
    }

    @GetMapping("/students-with-group/all")
    public ResponseEntity<List<BasicStudent>> getAllStudentsWithGroup(@RequestParam Long courseId) {
        return ResponseEntity.ok().body(userService.getAllStudentsWithGroup(courseId));
    }

    @PostMapping("/user/group/set")
    public ResponseEntity<Group> setUserGroup(@RequestBody SetStudentGroupForm setStudentGroupForm)
            throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(userService.updateStudentGroup(setStudentGroupForm));
    }

    @PostMapping("/user/group/join")
    public ResponseEntity<Group> joinGroup(@RequestBody JoinGroupDTO dto)
            throws WrongUserTypeException, EntityNotFoundException {
        userService.addUserToGroup(dto.getInvitationCode(), dto.getHeroType());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/index/set")
    public ResponseEntity<Integer> setUserIndex(@RequestBody SetStudentIndexForm setStudentIndexForm)
            throws WrongUserTypeException, EntityAlreadyInDatabaseException {
        return ResponseEntity.ok().body(userService.setIndexNumber(setStudentIndexForm));
    }

    @GetMapping("/professor/register/token")
    public ResponseEntity<String> getProfessorRegisterToken() throws WrongUserTypeException {
        return ResponseEntity.ok().body(userService.getProfessorRegisterToken());
    }

    @DeleteMapping("/user/delete-professor")
    public ResponseEntity<?> deleteProfessorAccount(@RequestParam String professorEmail) throws WrongUserTypeException {
        userService.deleteProfessorAccount(professorEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/user/delete-student")
    public ResponseEntity<?> deleteStudentAccount() throws WrongUserTypeException {
        userService.deleteStudentAccount();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/professor/emails")
    public ResponseEntity<List<String>> getAllProfEmails() {
        return ResponseEntity.ok().body(userService.getAllProfessorEmails());
    }

}
