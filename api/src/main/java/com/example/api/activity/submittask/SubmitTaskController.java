package com.example.api.activity.submittask;

import com.example.api.activity.CreateActivityChapterForm;
import com.example.api.error.exception.RequestValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/submit")
@SecurityRequirement(name = "JWT_AUTH")
public class SubmitTaskController {
    private final SubmitTaskService submitTaskService;
    private final SubmitTaskRepository rep;

    @GetMapping("/{id}")
    public ResponseEntity<?> aaaa(@PathVariable Long id) {
        return ResponseEntity.ok().body(rep.existsById(id));
    }

    @GetMapping("/create")
    public ResponseEntity<CreateSubmitTaskForm> getExampleSubmitTaskForm() {
        return ResponseEntity.ok().body(CreateSubmitTaskForm.example());
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createSubmitTask(@RequestBody CreateActivityChapterForm form)
            throws RequestValidationException {
        return ResponseEntity.ok().body(submitTaskService.createSubmitTask(form));
    }
}
