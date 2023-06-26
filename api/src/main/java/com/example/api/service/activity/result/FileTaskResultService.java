package com.example.api.service.activity.result;

import com.example.api.dto.request.activity.task.SaveFileToFileTaskResultForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.model.activity.result.FileTaskResult;
import com.example.api.model.activity.task.FileTask;
import com.example.api.model.user.User;
import com.example.api.model.util.File;
import com.example.api.repository.activity.result.FileTaskResultRepo;
import com.example.api.repository.activity.task.FileTaskRepository;
import com.example.api.repository.user.UserRepository;
import com.example.api.repository.util.FileRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.service.validator.UserValidator;
import com.example.api.service.validator.activity.ActivityValidator;
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
    private final FileTaskResultRepo fileTaskResultRepo;
    private final FileTaskRepository fileTaskRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final UserValidator userValidator;
    private final AuthenticationService authService;
    private final ActivityValidator activityValidator;

    public FileTaskResult saveFileTaskResult(FileTaskResult result) {
        return fileTaskResultRepo.save(result);
    }

    public Long saveFileToFileTaskResult(SaveFileToFileTaskResultForm form) throws EntityNotFoundException, WrongUserTypeException, IOException {
        log.info("Saving file to file task result with id {}", form.getFileTaskId());
        String email = authService.getAuthentication().getName();
        FileTaskResult result = getFileTaskResultByFileTaskAndUser(form.getFileTaskId(), email);
        if(result == null){
            result = new FileTaskResult();
            result.setAnswer("");
            result.setFileTask(fileTaskRepository.findFileTaskById(form.getFileTaskId()));
            result.setSendDateMillis(System.currentTimeMillis());
            result.setEvaluated(false);
            result.setUser(userRepository.findUserByEmail(email));
        }
        if(form.getFile() != null) {
            File file = new File(null, form.getFileName(), form.getFile().getBytes());
            fileRepository.save(file);
            result.getFiles().add(file);
            fileTaskResultRepo.save(result);
        }
        if(form.getOpenAnswer() != null) {
            result.setAnswer(form.getOpenAnswer());
            fileTaskResultRepo.save(result);
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
        userValidator.validateStudentAccount(student, email);
        return fileTaskResultRepo.findFileTaskResultByFileTaskAndUser(fileTask, student);
    }

    public ByteArrayResource getFileById(Long fileId) throws EntityNotFoundException {
        File file = fileRepository.findFileById(fileId);
        activityValidator.validateFileIsNotNull(file, fileId);
        return new ByteArrayResource(file.getFile());
    }

    public List<FileTaskResult> getAllFileTaskResultsForStudent(User student) {
        return fileTaskResultRepo.findAllByUser(student);
    }
}
