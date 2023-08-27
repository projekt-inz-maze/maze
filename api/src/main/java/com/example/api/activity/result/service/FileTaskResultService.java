package com.example.api.activity.result.service;

import com.example.api.activity.task.dto.request.SaveFileToFileTaskResultForm;
import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.user.model.User;
import com.example.api.util.model.File;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.util.repository.FileRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileTaskResultService {
    private final FileTaskResultRepository fileTaskResultRepository;
    private final FileTaskRepository fileTaskRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final ActivityValidator activityValidator;

    public FileTaskResult saveFileTaskResult(FileTaskResult result) {
        return fileTaskResultRepository.save(result);
    }

    public Long saveFileToFileTaskResult(SaveFileToFileTaskResultForm form) throws EntityNotFoundException, WrongUserTypeException, IOException {
        log.info("Saving file to file task result with id {}", form.getFileTaskId());
        String email = authService.getAuthentication().getName();
        FileTaskResult result = getFileTaskResultByFileTaskAndUser(form.getFileTaskId(), email);
        if (result == null) {
            result = new FileTaskResult();
            result.setAnswer("");
            result.setFileTask(fileTaskRepository.findFileTaskById(form.getFileTaskId()));
            result.setSendDateMillis(System.currentTimeMillis());
            result.setEvaluated(false);
        }
        if (form.getFile() != null) {
            File file = new File(null, form.getFileName(), result.getCourse(), form.getFile().getBytes());
            fileRepository.save(file);
            result.getFiles().add(file);
            fileTaskResultRepository.save(result);
        }
        if(form.getOpenAnswer() != null) {
            result.setAnswer(form.getOpenAnswer());
            fileTaskResultRepository.save(result);
        }
        return result.getId();
    }

    public Long deleteFileFromFileTask(Long fileTaskId, int index) throws EntityNotFoundException, WrongUserTypeException {
        log.info("Deleting file from file task result with id {}", fileTaskId);
        String email = authService.getAuthentication().getName();
        FileTaskResult result = getFileTaskResultByFileTaskAndUser(fileTaskId, email);
        result.getFiles().remove(index);
        return result.getId();
    }

    private FileTaskResult getFileTaskResultByFileTaskAndUser(Long fileTaskId, String email) throws EntityNotFoundException, WrongUserTypeException {
        FileTask fileTask = fileTaskRepository.findFileTaskById(fileTaskId);
        activityValidator.validateActivityIsNotNull(fileTask, fileTaskId);
        User student = userRepository.findUserByEmail(email);
        userValidator.validateStudentAccount(student);
        return fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, student);
    }

    public ByteArrayResource getFileById(Long fileId) throws EntityNotFoundException {
        File file = fileRepository.findFileById(fileId);
        activityValidator.validateFileIsNotNull(file, fileId);
        return new ByteArrayResource(file.getFile());
    }

    public List<FileTaskResult> getAllFileTaskResultsForStudent(User student, Course course) {
        return fileTaskResultRepository.findAllByUserAndCourse(student, course);
    }
}
