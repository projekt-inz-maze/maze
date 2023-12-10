package com.example.api.activity.result.controller;

import com.example.api.activity.result.dto.SaveFileToFileTaskResultForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.service.FileTaskResultService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/file/result")
@SecurityRequirement(name = "JWT_AUTH")
public class FileTaskResultController {
    private final FileTaskResultService fileTaskResultService;

    @PostMapping(path="/file/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> saveFileTaskResult(@ModelAttribute SaveFileToFileTaskResultForm form)
            throws EntityNotFoundException, WrongUserTypeException, IOException {
        return ResponseEntity.ok().body(fileTaskResultService.saveFileToFileTaskResult(form));
    }

    @DeleteMapping("/file/delete")
    public ResponseEntity<Long> deleteFileFromFileTask(@RequestParam Long fileTaskId, @RequestParam int index)
            throws EntityNotFoundException, WrongUserTypeException {
        return ResponseEntity.ok().body(fileTaskResultService.deleteFileFromFileTask(fileTaskId, index));
    }

    @GetMapping("/file")
    ResponseEntity<ByteArrayResource> getFileById(@RequestParam Long fileId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(fileTaskResultService.getFileById(fileId));
    }
}
