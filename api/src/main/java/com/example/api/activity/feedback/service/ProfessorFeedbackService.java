package com.example.api.activity.feedback.service;


import com.example.api.activity.feedback.dto.request.SaveProfessorFeedbackForm;
import com.example.api.activity.feedback.dto.response.ProfessorFeedbackInfoResponse;
import com.example.api.activity.task.dto.response.util.FileResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongPointsNumberException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.model.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.user.model.User;
import com.example.api.util.model.File;
import com.example.api.activity.feedback.repository.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.validator.FeedbackValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfessorFeedbackService {
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final FeedbackValidator feedbackValidator;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final FileTaskRepository fileTaskRepository;
    private final UserRepository userRepository;
    private final ActivityValidator activityValidator;
    private final UserValidator userValidator;

    public ProfessorFeedbackInfoResponse saveProfessorFeedback(ProfessorFeedback feedback)
            throws MissingAttributeException, EntityNotFoundException {
        return createInfoResponseFromProfessorFeedback(professorFeedbackRepository.save(feedback));
    }

    public ProfessorFeedbackInfoResponse saveProfessorFeedback(SaveProfessorFeedbackForm form)
            throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException, WrongPointsNumberException, IOException {
        log.info("Saving professor feedback to database");
        ProfessorFeedback professorFeedback =
                feedbackValidator.validateAndSetProfessorFeedbackTaskForm(form);
        log.debug(professorFeedback.getContent());

        return createInfoResponseFromProfessorFeedback(professorFeedbackRepository.save(professorFeedback));
    }

    public ProfessorFeedback getProfessorFeedbackForFileTaskResult(Long id)
            throws EntityNotFoundException {
        log.info("Fetching professor feedback for file task result with id {}", id);
        FileTaskResult result = fileTaskResultRepository.findFileTaskResultById(id);
        activityValidator.validateTaskResultIsNotNull(result, id);
        return professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(result);
    }

    public ProfessorFeedbackInfoResponse getProfessorFeedbackInfoForFileTaskResult(Long id)
            throws EntityNotFoundException, MissingAttributeException {
        return createInfoResponseFromProfessorFeedback(getProfessorFeedbackForFileTaskResult(id));

    }

    public ProfessorFeedback getProfessorFeedbackForFileTaskAndStudent(Long fileTaskId, String studentEmail)
            throws EntityNotFoundException, WrongUserTypeException {
        log.info("Fetching professor feedback for file task with id {} and student {}", fileTaskId, studentEmail);
        FileTask fileTask = fileTaskRepository.findFileTaskById(fileTaskId);
        activityValidator.validateActivityIsNotNull(fileTask, fileTaskId);
        User student = userRepository.findUserByEmail(studentEmail);
        userValidator.validateStudentAccount(student);
        FileTaskResult result = fileTaskResultRepository.findFileTaskResultByFileTaskAndUser(fileTask, student);
        activityValidator.validateTaskResultIsNotNull(result, student, fileTask);
        return professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(result);
    }

    public ProfessorFeedbackInfoResponse getProfessorFeedbackInfoForFileTaskAndStudent(Long fileTaskId, String studentEmail)
            throws EntityNotFoundException, MissingAttributeException, WrongUserTypeException {
        return createInfoResponseFromProfessorFeedback(getProfessorFeedbackForFileTaskAndStudent(fileTaskId, studentEmail));
    }

    private ProfessorFeedbackInfoResponse createInfoResponseFromProfessorFeedback(
            ProfessorFeedback professorFeedback) throws MissingAttributeException, EntityNotFoundException {
        FileTaskResult fileTaskResult = professorFeedback.getFileTaskResult();
        User student = professorFeedback.getFileTaskResult().getUser();
        FileTask fileTask = professorFeedback.getFileTaskResult().getFileTask();
        feedbackValidator.validateFeedbackForInfoResponse(professorFeedback, fileTaskResult, student, fileTask);

        ProfessorFeedbackInfoResponse infoResponse = new ProfessorFeedbackInfoResponse();
        infoResponse.setFeedbackId(professorFeedback.getId());
        infoResponse.setFileTaskResultId(fileTaskResult.getId());
        infoResponse.setStudentEmail(student.getEmail());
        infoResponse.setFileTaskId(fileTask.getId());
        infoResponse.setTaskName(fileTask.getTitle());
        infoResponse.setDescription(fileTask.getDescription());
        infoResponse.setAnswer(fileTaskResult.getAnswer());
        infoResponse.setTaskFiles(fileTaskResult.getFiles()
                .stream()
                .map(FileResponse::new)
                .collect(Collectors.toList()));
        infoResponse.setPoints(fileTaskResult.getPointsReceived());
        infoResponse.setRemarks(professorFeedback.getContent());
        File feedbackFile = professorFeedback.getFeedbackFile();

        if (feedbackFile != null) {
            infoResponse.setFeedbackFile(new FileResponse(professorFeedback.getFeedbackFile()));
        }
        return infoResponse;
    }
}
