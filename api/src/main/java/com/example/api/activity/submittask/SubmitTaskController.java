package com.example.api.activity.submittask;

import com.example.api.activity.CreateSubmitTaskChapterForm;
import com.example.api.activity.submittask.result.SubmitTaskResultDTO;
import com.example.api.activity.submittask.result.SubmitTaskResultWithFileDTO;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/submit")
@SecurityRequirement(name = "JWT_AUTH")
public class SubmitTaskController {
    private final SubmitTaskService submitTaskService;

    @GetMapping("/create")
    public ResponseEntity<CreateSubmitTaskForm> getExampleSubmitTaskForm() {
        return ResponseEntity.ok().body(CreateSubmitTaskForm.example());
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createSubmitTask(@RequestBody CreateSubmitTaskChapterForm form)
            throws RequestValidationException {
        return ResponseEntity.ok().body(submitTaskService.createSubmitTask(form));
    }

    @PostMapping("/result")
    public ResponseEntity<Long> addResult(@RequestBody SubmitTaskResultDTO dto) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(submitTaskService.createResultForSubmitTask(dto));
    }

    @PostMapping(path="/add-result", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> saveFileTaskResult(@ModelAttribute SubmitTaskResultWithFileDTO dto) throws WrongUserTypeException, EntityNotFoundException, IOException {
        return ResponseEntity.ok().body(submitTaskService.createResultForSubmitTask(dto));
    }

    @PostMapping("/result/{id}")
    public ResponseEntity<?> rejectResult(@PathVariable Long id, @RequestParam boolean accept) {
        if (accept) {
           return ResponseEntity.ok(submitTaskService.acceptResult(id));
        } else {
            submitTaskService.rejectResult(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
