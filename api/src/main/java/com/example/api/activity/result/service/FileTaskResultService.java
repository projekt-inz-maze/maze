package com.example.api.activity.result.service;

import com.example.api.activity.result.dto.SaveFileToFileTaskResultForm;
import com.example.api.course.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.file.FileService;
import com.example.api.user.model.User;
import com.example.api.file.File;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.file.FileRepository;
import com.example.api.security.LoggedInUserService;
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
    private final LoggedInUserService authService;
    private final ActivityValidator activityValidator;
    private final FileService fileService;

    public FileTaskResult saveFileTaskResult(FileTaskResult result) {
        return fileTaskResultRepository.save(result);
    }

    public Long saveFileToFileTaskResult(SaveFileToFileTaskResultForm form) throws EntityNotFoundException, WrongUserTypeException, IOException {
        log.info("Saving file to file task result with id {}", form.getFileTaskId());
        User user = authService.getCurrentUser();

        FileTaskResult result = getFileTaskResultByFileTaskAndUser(form.getFileTaskId(), user.getEmail());

        if (result == null) {
            FileTask task = fileTaskRepository.findFileTaskById(form.getFileTaskId());

            result = new FileTaskResult();
            result.setAnswer("");
            result.setFileTask(fileTaskRepository.findFileTaskById(form.getFileTaskId()));
            result.setSendDateMillis(System.currentTimeMillis());
            result.setMember(user.getCourseMember(task.getCourse().getId(), true));
            result.setCourse(task.getCourse());
            result.setEvaluated(false);
        }

        if (form.getFile() != null) {
            File file = new File(null, form.getFileName(), form.getFile().getBytes());
            fileRepository.save(file);
            result.getFiles().add(file);
            fileTaskResultRepository.save(result);
        }

        if (form.getOpenAnswer() != null) {
            result.setAnswer(form.getOpenAnswer());
            fileTaskResultRepository.save(result);
        }
        return result.getId();
    }

    public Long deleteFileFromFileTask(Long fileTaskId, int index) throws EntityNotFoundException, WrongUserTypeException {
        log.info("Deleting file from file task result with id {}", fileTaskId);
        String email = authService.getCurrentUser().getEmail();
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
        return fileService.getFileById(fileId);
    }

    public List<FileTaskResult> getAllFileTaskResultsForStudent(User student, Course course) {
        return fileTaskResultRepository.findAllByMember_UserAndCourse(student, course);
    }
}
