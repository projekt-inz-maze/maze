package com.example.api.activity;

import com.example.api.activity.info.EditInfoForm;
import com.example.api.activity.info.Info;
import com.example.api.activity.info.InfoRepository;
import com.example.api.activity.info.InfoService;
import com.example.api.activity.survey.EditSurveyForm;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.survey.SurveyRepository;
import com.example.api.activity.survey.SurveyService;
import com.example.api.activity.task.filetask.EditFileTaskForm;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.filetask.FileTaskRepository;
import com.example.api.activity.task.filetask.FileTaskService;
import com.example.api.activity.task.graphtask.EditGraphTaskForm;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.task.graphtask.GraphTaskRepository;
import com.example.api.activity.task.graphtask.GraphTaskService;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.chapter.Chapter;
import com.example.api.chapter.ChapterRepository;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.file.File;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityService {
    private final LoggedInUserService authService;
    private final UserValidator userValidator;
    private final GraphTaskRepository graphTaskRepository;
    private final FileTaskRepository fileTaskRepository;
    private final SurveyRepository surveyRepository;
    private final InfoRepository infoRepository;
    private final ActivityValidator activityValidator;
    private final GraphTaskService graphTaskService;
    private final FileTaskService fileTaskService;
    private final InfoService infoService;
    private final SurveyService surveyService;
    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;
    private final ActivityRepository activityRepository;

    public EditActivityForm getActivityEditInfo(Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        Activity activity = getActivity(activityID);
        activityValidator.validateActivityIsNotNull(activity, activityID);

        return toEditActivityForm(activity);
    }

    public void editActivity(EditActivityForm form) throws RequestValidationException, ParseException {
        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        Activity activity = getActivity(form.getActivityID());
        activityValidator.validateActivityIsNotNull(activity, form.getActivityID());

        log.info("Professor {} try to edit activity {} with id {}",
                professor.getEmail(), activity.getActivityType().getActivityType(), activity.getId());

        editActivity(activity, form);
        switch (activity.getActivityType()) {
            case EXPEDITION -> graphTaskService.editGraphTask((GraphTask) activity, (EditGraphTaskForm) form);
            case TASK -> fileTaskService.editFileTask((FileTask) activity, (EditFileTaskForm) form);
            case INFO -> infoService.editInfo((Info) activity, (EditInfoForm) form);
            case SURVEY -> surveyService.editSurvey((Survey) activity, (EditSurveyForm) form);
        }
    }

    private EditActivityForm toEditActivityForm(Activity activity) {
        switch (activity.getActivityType()) {
            case EXPEDITION -> {
                return new EditGraphTaskForm((GraphTask) activity);
            }
            case TASK -> {
                return new EditFileTaskForm((FileTask) activity);
            }
            case SURVEY -> {
                return new EditSurveyForm((Survey) activity);
            }
            case INFO -> {
                return new EditInfoForm((Info) activity);
            }
            default -> {
                log.error("Cannot create EditActivityForm for given activity with type {}", activity.getActivityType());
                throw new IllegalArgumentException("Cannot create EditActivityForm for given activity with type" + activity.getActivityType());
            }
        }
    }

    public void editActivity(Activity activity, EditActivityForm form) throws RequestValidationException {
        CreateActivityForm editForm = form.getActivityBody();

        Chapter chapter = chapterRepository.findAll()
                .stream()
                .filter(ch -> ch.getActivityMap().hasActivity(activity))
                .findFirst()
                .orElse(null);

        activity.setTitle(editForm.getTitle());
        activity.setDescription(editForm.getDescription());
        chapterValidator.validateChapterIsNotNull(chapter, null);

        if (activity.getPosX().equals(editForm.getPosX()) &&
                activity.getPosY().equals(editForm.getPosY())) {
            return;
        }
        activityValidator.validateActivityPosition(editForm, chapter);
        activity.setPosX(editForm.getPosX());
        activity.setPosY(editForm.getPosY());
    }

    public void deleteActivity(Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        User professor = authService.getCurrentUser();
        userValidator.validateProfessorAccount(professor);

        Activity activity = getActivity(activityID);
        activityValidator.validateActivityIsNotNull(activity, activityID);
        switch (activity.getActivityType()) {
            case EXPEDITION -> graphTaskRepository.delete((GraphTask) activity);
            case TASK -> fileTaskRepository.delete((FileTask) activity);
            case INFO -> infoRepository.delete((Info) activity);
            case SURVEY -> surveyRepository.delete((Survey) activity);
        }
    }

    public Activity getActivity(Long activityId) throws EntityNotFoundException {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    public void addFile(Activity activity, File file) {
        activity.addFile(file);
        activityRepository.save(activity);
    }
}
