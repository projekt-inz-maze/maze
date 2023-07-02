package com.example.api.unit.service.activity.feedback;

import com.example.api.activity.feedback.dto.request.SaveProfessorFeedbackForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongPointsNumberException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.feedback.model.ProfessorFeedback;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.user.model.User;
import com.example.api.activity.feedback.repository.ProfessorFeedbackRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.task.repository.FileTaskRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.activity.feedback.service.ProfessorFeedbackService;
import com.example.api.validator.FeedbackValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// TODO: update and complete tests
public class ProfessorFeedbackServiceTest {
    private ProfessorFeedbackService professorFeedbackService;
    @Mock private ProfessorFeedbackRepository professorFeedbackRepository;
    @Mock private FeedbackValidator feedbackValidator;
    @Mock private FileTaskResultRepository fileTaskResultRepository;
    @Mock private FileTaskRepository fileTaskRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActivityValidator activityValidator;
    @Mock private UserValidator userValidator;
    @Captor private ArgumentCaptor<ProfessorFeedback> professorFeedbackArgumentCaptor;
    @Captor private ArgumentCaptor<SaveProfessorFeedbackForm> formArgumentCaptor;
    @Captor private ArgumentCaptor<Long> idArgumentCaptor;
    @Captor private ArgumentCaptor<FileTaskResult> fileTaskResultArgumentCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        professorFeedbackService = new ProfessorFeedbackService(
                professorFeedbackRepository,
                feedbackValidator,
                fileTaskResultRepository,
                fileTaskRepository,
                userRepository,
                activityValidator,
                userValidator);
    }

    @Test
    public void saveProfessorFeedback() throws MissingAttributeException, EntityNotFoundException {
        //given
        ProfessorFeedback feedback = new ProfessorFeedback();
        User user = new User();
        user.setEmail("email");
        FileTask fileTask = new FileTask();
        fileTask.setId(1L);
        FileTaskResult fileTaskResult = new FileTaskResult();
        fileTaskResult.setUser(user);
        fileTaskResult.setFileTask(fileTask);
        feedback.setFileTaskResult(fileTaskResult);

        given(professorFeedbackRepository.save(feedback)).willReturn(feedback);

        //when
        professorFeedbackService.saveProfessorFeedback(feedback);

        //then
        verify(professorFeedbackRepository).save(professorFeedbackArgumentCaptor.capture());
        ProfessorFeedback capturedFeedback = professorFeedbackArgumentCaptor.getValue();
        assertThat(capturedFeedback).isEqualTo(feedback);
    }

    @Test
    public void saveProfessorFeedbackForm() throws WrongUserTypeException, EntityNotFoundException, IOException, MissingAttributeException, WrongPointsNumberException {
        //given
        SaveProfessorFeedbackForm form = new SaveProfessorFeedbackForm();
        form.setContent("random content");
        form.setPoints(10.0);
        form.setFileTaskResultId(2L);
        ProfessorFeedback feedback = new ProfessorFeedback();
        User user = new User();
        user.setEmail("email");
        FileTask fileTask = new FileTask();
        fileTask.setId(1L);
        FileTaskResult fileTaskResult = new FileTaskResult();
        fileTaskResult.setUser(user);
        fileTaskResult.setFileTask(fileTask);
        feedback.setFileTaskResult(fileTaskResult);
        given(feedbackValidator.validateAndSetProfessorFeedbackTaskForm(form)).willReturn(feedback);
        given(professorFeedbackRepository.save(feedback)).willReturn(feedback);

        //when
        professorFeedbackService.saveProfessorFeedback(form);

        //then
        verify(feedbackValidator).validateAndSetProfessorFeedbackTaskForm(formArgumentCaptor.capture());
        verify(professorFeedbackRepository).save(professorFeedbackArgumentCaptor.capture());
        SaveProfessorFeedbackForm capturedForm = formArgumentCaptor.getValue();
        ProfessorFeedback capturedFeedback = professorFeedbackArgumentCaptor.getValue();
        assertThat(capturedForm).isEqualTo(form);
        assertThat(capturedFeedback).isEqualTo(feedback);
    }

    @Test
    public void getProfessorFeedbackForFileTask() throws EntityNotFoundException, MissingAttributeException {
        //given
        Long id = 1L;
        FileTaskResult result = new FileTaskResult();
        given(fileTaskResultRepository.findFileTaskResultById(id)).willReturn(result);

        //when
        professorFeedbackService.getProfessorFeedbackForFileTaskResult(id);

        //then
        verify(fileTaskResultRepository).findFileTaskResultById(idArgumentCaptor.capture());
        verify(professorFeedbackRepository).findProfessorFeedbackByFileTaskResult(fileTaskResultArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();
        FileTaskResult capturedResult = fileTaskResultArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(capturedResult).isEqualTo(result);
    }

}
