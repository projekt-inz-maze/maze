package com.example.api.chapter;

import com.example.api.activity.task.RequirementForm;
import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.chapter.requirement.RequirementDTO;
import com.example.api.chapter.requirement.RequirementResponse;
import com.example.api.chapter.response.ChapterInfoResponse;
import com.example.api.chapter.response.ChapterResponse;
import com.example.api.chapter.response.ChapterResponseProfessor;
import com.example.api.chapter.response.ChapterResponseStudent;
import com.example.api.map.mapactivity.MapActivity;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.Activity;
import com.example.api.map.ActivityMap;
import com.example.api.map.ActivityMapService;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.util.model.File;
import com.example.api.util.repository.FileRepository;
import com.example.api.validator.ChapterValidator;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final ActivityMapService activityMapService;
    private final FileRepository fileRepository;
    private final ActivityValidator activityValidator;
    private final ChapterValidator chapterValidator;
    private final RequirementService requirementService;
    private final CourseValidator courseValidator;
    private final CourseService courseService;
    private final LoggedInUserService authService;

    public List<? extends ChapterResponse> getAllChapters(Long courseId) throws EntityNotFoundException {
        User user = authService.getCurrentUser();
        courseValidator.validateUserCanAccess(user, courseId);
        log.info("Fetching all chapters for course {}, for user {}", courseId, user.getId());

        Course course = courseService.getCourse(courseId);
        List<Chapter> chapters = chapterRepository.findAllByCourse(course);
        AccountType accountType = user.getAccountType();

        if (accountType == AccountType.STUDENT) {
            return chapters.stream()
                    .filter(chapter -> !chapter.getIsBlocked())
                    .map(chapter ->
                            new ChapterResponseStudent(
                                    chapter,
                                    requirementService.areRequirementsFulfilled(chapter.getRequirements(), chapter.getCourse())
                            )
                    )
                    .sorted(Comparator.comparingLong(ChapterResponse::getId))
                    .toList();
        } else {
            return chapters.stream()
                    .map(ChapterResponseProfessor::new)
                    .sorted(Comparator.comparingLong(ChapterResponse::getId))
                    .toList();
        }
    }

    public ChapterInfoResponse getChapterInfo(Long id) throws EntityNotFoundException {

        log.info("Fetching info for chapter with id {}", id);
        Chapter chapter = chapterRepository.findChapterById(id);
        chapterValidator.validateChapterIsNotNull(chapter, id);

        User user = authService.getCurrentUser();
        chapterValidator.validateUserCanAccess(user, chapter);

        List<? extends MapActivity> allTasks;
        if (user.getAccountType() == AccountType.STUDENT){
            allTasks = activityMapService.getMapTasksForStudent(chapter.getActivityMap(), user);
        } else {
            allTasks = activityMapService.getMapTasksForProfessor(chapter.getActivityMap());
        }

        return new ChapterInfoResponse(chapter, allTasks);
    }

    public void createChapter(ChapterForm form) throws RequestValidationException {
        User user = authService.getCurrentUser();
        Course course = courseService.getCourse(form.getCourseId());
        courseValidator.validateCourseOwner(course, user);
        log.info("Creating new chapter");

        File image = fileRepository.findFileById(form.getImageId());
        activityValidator.validateFileIsNotNull(image, form.getImageId());
        chapterValidator.validateChapterCreation(form);
        ActivityMap activityMap = new ActivityMap(form.getSizeX(), form.getSizeY(), image);
        activityMapService.saveActivityMap(activityMap);
        Chapter chapter = new Chapter(form.getName(), activityMap, form.getPosX(), form.getPosY(), course);
        chapter.setRequirements(requirementService.getDefaultRequirements(false));

        chapterRepository.save(chapter);

        Optional.ofNullable(chapterRepository.findFirstByCourseIsAndNextChapterIsNull(course))
                .ifPresent(prevChapter -> prevChapter.setNextChapter(chapter));
    }

    public Chapter getChapterWithActivity(Activity activity) {
        return chapterRepository.findAll()
                .stream()
                .filter(chapter -> chapter.getActivityMap().hasActivity(activity))
                .findAny()
                .orElse(null);
    }

    public void deleteChapter(Long chapterID) throws RequestValidationException {
        User professor = authService.getCurrentUser();
        Chapter chapter = chapterRepository.findChapterById(chapterID);
        chapterValidator.validateChapterIsNotNull(chapter, chapterID);
        courseValidator.validateCourseOwner(chapter.getCourse(), professor);

        Optional<Chapter> prevChapter = chapterRepository.findAll().stream().filter(ch -> chapter.equals(ch.getNextChapter()))
                .findAny();
        prevChapter.ifPresent(value -> value.setNextChapter(chapter.getNextChapter()));
        chapterRepository.deleteChapterById(chapterID);
    }

    public void editChapter(EditChapterForm editChapterForm) throws RequestValidationException {
        // user validation


        ChapterForm chapterForm = editChapterForm.getEditionForm();

        // chapter validation for given editChapterForm
        User professor = authService.getCurrentUser();
        Chapter chapter = chapterRepository.findChapterById(editChapterForm.getChapterId());
        chapterValidator.validateChapterIsNotNull(chapter, editChapterForm.getChapterId());
        courseValidator.validateCourseOwner(chapter.getCourse(), professor);

        chapterValidator.validatePositionTaken(chapterForm, chapter);
        chapterValidator.validateChapterEdition(chapterForm, chapter);
        chapterValidator.validateImageExists(chapterForm.getImageId());

        // edit basic chapter data
        chapter.setName(chapterForm.getName());
        chapter.setPosX(chapterForm.getPosX());
        chapter.setPosY(chapterForm.getPosY());

        // edit chapter activity map
        ActivityMap chapterMap = chapter.getActivityMap();
        chapterMap.setMapSizeX(chapterForm.getSizeX());
        chapterMap.setMapSizeY(chapterForm.getSizeY());
        chapterMap.setImage(fileRepository.findFileById(chapterForm.getImageId()));

        chapter.setActivityMap(chapterMap);
    }

    public RequirementResponse getRequirementsForChapter(Long chapterId) throws EntityNotFoundException {

        Chapter chapter = chapterRepository.findChapterById(chapterId);
        chapterValidator.validateChapterIsNotNull(chapter, chapterId);

        User user = authService.getCurrentUser();
        chapterValidator.validateUserCanAccess(user, chapter);

        List<? extends RequirementDTO<?>> requirements = chapter.getRequirements()
                .stream()
                .map(Requirement::getResponse)
                .sorted(Comparator.comparingLong(RequirementDTO::getId))
                .toList();
        return new RequirementResponse(chapter.getIsBlocked(), requirements);
    }

    public void updateRequirementForChapter(ChapterRequirementForm form) throws RequestValidationException {
        Chapter chapter = chapterRepository.findChapterById(form.getChapterId());
        chapterValidator.validateChapterIsNotNull(chapter, form.getChapterId());

        User user = authService.getCurrentUser();
        chapterValidator.validateUserCanAccess(user, chapter);

        Boolean isBlocked = form.getIsBlocked();

        if (isBlocked != null) {
            chapter.setIsBlocked(isBlocked);
        }
        List<RequirementForm> requirementForms = form.getRequirements();
        requirementService.updateRequirements(requirementForms);
    }

    public List<? extends Activity> getAllActivitiesForChapter(Chapter chapter) {
        return Stream.of(chapter.getActivityMap().getGraphTasks(),
                    chapter.getActivityMap().getFileTasks(),
                    chapter.getActivityMap().getInfos(),
                    chapter.getActivityMap().getSurveys()
                )
                .flatMap(Collection::stream)
                .toList();
    }
}
