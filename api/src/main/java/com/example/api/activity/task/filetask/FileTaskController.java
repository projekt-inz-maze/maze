package com.example.api.activity.task.filetask;

import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/file")
@SecurityRequirement(name = "JWT_AUTH")
public class FileTaskController {
    private final FileTaskService fileTaskService;

    @GetMapping
    public ResponseEntity<FileTaskDetailsResponse> getFileTaskById(@RequestParam Long fileTaskId)
            throws EntityNotFoundException, WrongUserTypeException {
        return ResponseEntity.ok().body(fileTaskService.getFileTaskInfo(fileTaskId));
    }

    @GetMapping("/create")
    public ResponseEntity<CreateFileTaskForm> getExampleFileTaskForm() {
        return ResponseEntity.ok().body(CreateFileTaskForm.example());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFileTask(@RequestBody CreateFileTaskChapterForm form)
            throws RequestValidationException {
        fileTaskService.createFileTask(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
