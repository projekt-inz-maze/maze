package com.example.api.map.controller;

import com.example.api.map.dto.request.ChapterForm;
import com.example.api.map.dto.request.ChapterRequirementForm;
import com.example.api.map.dto.request.EditChapterForm;
import com.example.api.map.dto.response.RequirementResponse;
import com.example.api.map.dto.response.chapter.ChapterInfoResponse;
import com.example.api.map.dto.response.chapter.ChapterResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.map.service.ChapterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chapter")
@SecurityRequirement(name = "JWT_AUTH")
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping
    public ResponseEntity<List<? extends ChapterResponse>> getAllChapters(@RequestParam Long courseId) {
        return ResponseEntity.ok().body(chapterService.getAllChapters());
    }

    @GetMapping("/info")
    public ResponseEntity<ChapterInfoResponse> getChapterInfo(@RequestParam Long id) throws EntityNotFoundException {
        return ResponseEntity.ok().body(chapterService.getChapterInfo(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChapter(@RequestBody ChapterForm form) throws RequestValidationException {
        chapterService.createChapter(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteChapter(@RequestParam Long chapterID) throws WrongUserTypeException, EntityNotFoundException {
        chapterService.deleteChapter(chapterID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editChapter(@RequestBody EditChapterForm editChapterForm) throws RequestValidationException {
        chapterService.editChapter(editChapterForm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/requirements")
    ResponseEntity<RequirementResponse> getRequirementsForChapter(@RequestParam Long chapterId)
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(chapterService.getRequirementsForChapter(chapterId));
    }

    @PostMapping("/requirements/update")
    ResponseEntity<?> updateRequirementForChapter(@RequestBody ChapterRequirementForm form) throws RequestValidationException {
        chapterService.updateRequirementForChapter(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
