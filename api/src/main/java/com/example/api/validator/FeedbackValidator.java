package com.example.api.validator;

import com.example.api.activity.auction.Auction;
import com.example.api.activity.feedback.SaveProfessorFeedbackForm;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongPointsNumberException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.user.model.User;
import com.example.api.file.File;
import com.example.api.activity.feedback.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.file.FileRepository;
import com.example.api.security.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FeedbackValidator {
    private final ProfessorFeedbackRepository professorFeedbackRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final LoggedInUserService authService;
    private final FileRepository fileRepository;
    private final UserValidator userValidator;

    /**
     * Function creates professor feedback for fileTaskResult. If feedback already exists its attributes
     * are set according to non-nullable parameters of given form. Content and points are overwritten
     * but files are added to list
    */
    public ProfessorFeedback validateAndSetProfessorFeedbackTaskForm(SaveProfessorFeedbackForm form)
            throws WrongUserTypeException, EntityNotFoundException, WrongPointsNumberException, IOException {

        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        Long id = form.getFileTaskResultId();
        FileTaskResult fileTaskResult = fileTaskResultRepository.findFileTaskResultById(id);
        if (fileTaskResult == null) {
            log.error("File task result with id {} not found in database", id);
            throw new EntityNotFoundException("File task result with id " + id + " not found in database");
        }
        ProfessorFeedback feedback = professorFeedbackRepository.findProfessorFeedbackByFileTaskResult(fileTaskResult);

        FileTask task = fileTaskResult.getFileTask();

        if (feedback == null) {
            feedback = new ProfessorFeedback();
            feedback.setFrom(professor);
            feedback.setFileTaskResult(fileTaskResult);

        }
        if (form.getContent() != null) {
            feedback.setContent(form.getContent());
        }

        if (form.getPoints() != null) {
            if (form.getPoints() < 0 || form.getPoints() > task.getMaxPoints()) {
                throw new WrongPointsNumberException("Wrong points number", form.getPoints(), task.getMaxPoints());
            }
            feedback.setPoints(form.getPoints());
            fileTaskResult.setPoints(form.getPoints());
            fileTaskResultRepository.save(fileTaskResult);

            task.getAuction().flatMap(Auction::getHighestBid).ifPresent(bid -> bid.returnPoints(form.getPoints()));
            task.getAuthoredByStudent().ifPresent(authored -> authored.setPoints(form.getPoints()));
        }

        // Feedback file can be set only once
        if(feedback.getFeedbackFile() == null && form.getFile() != null) {
            File file = new File(null, form.getFileName(), fileTaskResult.getCourse(), form.getFile().getBytes());
            fileRepository.save(file);
            feedback.setFeedbackFile(file);
        }

        fileTaskResult.setEvaluated(true);
        fileTaskResultRepository.save(fileTaskResult);

        return professorFeedbackRepository.save(feedback);
    }

    public void validateFeedbackForInfoResponse(ProfessorFeedback professorFeedback,
                                                FileTaskResult fileTaskResult,
                                                CourseMember member,
                                                FileTask fileTask)
            throws EntityNotFoundException, MissingAttributeException {
        if (professorFeedback == null) {
            String msg = "Professor feedback doesn't exist";
            throw new EntityNotFoundException(msg);
        }
        if (fileTaskResult == null) {
            String msg = "Professor feedback with id " + professorFeedback.getId() + " is missing fileTaskResult attribute";
            throw new MissingAttributeException(msg);
        }
        if (member == null) {
            String msg = "Professor feedback with id " + professorFeedback.getId() + " is missing member attribute";
            throw new MissingAttributeException(msg);
        }

        if (fileTask == null) {
            String msg = "Professor feedback with id " + professorFeedback.getId() + " is missing fileTask attribute";
            throw new MissingAttributeException(msg);
        }
    }
}
