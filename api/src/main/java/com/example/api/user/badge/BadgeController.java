package com.example.api.user.badge;

import com.example.api.user.badge.dtos.BadgeAddForm;
import com.example.api.user.badge.dtos.BadgeUpdateForm;
import com.example.api.user.dto.response.badge.BadgeResponse;
import com.example.api.user.dto.response.badge.UnlockedBadgeResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/badge")
@SecurityRequirement(name = "JWT_AUTH")
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping("/all")
    public ResponseEntity<List<? extends BadgeResponse<?>>> getAllBadges(@RequestParam Long courseId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(badgeService.getAllBadges(courseId));
    }

    @GetMapping("/unlocked/all")
    public ResponseEntity<List<UnlockedBadgeResponse>> getAllUnlockedBadges(@RequestParam Long courseId) throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        return ResponseEntity.ok().body(badgeService.getAllUnlockedBadges(courseId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBadge(@RequestParam Long badgeId){
        badgeService.deleteBadge(badgeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateBadge(@ModelAttribute BadgeUpdateForm form) throws IOException, RequestValidationException {
        badgeService.updateBadge(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addBadge(@ModelAttribute BadgeAddForm form) throws IOException, RequestValidationException {
        badgeService.addBadge(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
