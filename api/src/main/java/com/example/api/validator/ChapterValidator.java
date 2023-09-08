package com.example.api.validator;

import com.example.api.course.model.Course;
import com.example.api.map.dto.request.ChapterForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.task.model.Activity;
import com.example.api.map.model.ActivityMap;
import com.example.api.map.model.Chapter;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.util.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChapterValidator {
    private final ChapterRepository chapterRepository;
    private final FileRepository fileRepository;

    public void validateChapterIsNotNull(Chapter chapter, Long id) throws EntityNotFoundException {
        if(chapter == null) {
            log.error("Chapter with id {} not found in database", id);
            throw new EntityNotFoundException("Chapter with id" + id + " not found in database");
        }
    }

    public void validateChapterCreation(ChapterForm form) throws RequestValidationException {
        List<Chapter> chapters = chapterRepository.findAll();
        if (chapters.stream()
                .anyMatch(chapter ->
                                Objects.equals(chapter.getPosX(), form.getPosX()) &&
                                Objects.equals(chapter.getPosY(), form.getPosY()))) {
            log.error("Two chapters cannot be on the same position!");
            throw new RequestValidationException(ExceptionMessage.TWO_CHAPTERS_ON_THE_SAME_POSITION);
        }
    }

    public void validateChapterEdition(ChapterForm form, Chapter chapter) throws RequestValidationException{
        ActivityMap chapterMap = chapter.getActivityMap();

        Integer newSizeX = form.getSizeX();
        Integer newSizeY = form.getSizeY();

        List<? extends Activity> activities = Stream.of(chapterMap.getSurveys(),
                        chapterMap.getInfos(),
                        chapterMap.getGraphTasks(),
                        chapterMap.getFileTasks())
                .flatMap(Collection::stream)
                .toList();

        // checking that the activities do not go beyond the map
        if (activities.stream().anyMatch(activity -> activity.getPosX() >= newSizeX || activity.getPosY() >= newSizeY)){
            log.error("New chapter size is too small for chapter " + chapter.getId());
            throw new RequestValidationException(ExceptionMessage.CHAPTER_MAP_SIZE_TOO_SMALL);
        }
    }

    public void validatePositionTaken(ChapterForm form, Chapter chapter) throws RequestValidationException {
        List<Chapter> chapters = chapterRepository.findAll();

        if (chapters.stream().anyMatch(chapter_ ->
                Objects.equals(chapter_.getPosX(), form.getPosX()) &&
                Objects.equals(chapter_.getPosY(), form.getPosY()) &&
                !Objects.equals(chapter.getId(), chapter_.getId()))){
            log.error("Two chapters cannot be on the same position!");
            throw new RequestValidationException(ExceptionMessage.TWO_CHAPTERS_ON_THE_SAME_POSITION);
        }
    }

    public void validateImageExists(Long newImageId) throws RequestValidationException {
        if (fileRepository.findFileById(newImageId) == null){
            log.error("Image with id {} not found in database", newImageId);
            throw new RequestValidationException(ExceptionMessage.IMAGE_NOT_EXISTS);
        }
    }

    public void validateUserCanAccess(User user, Chapter chapter) throws EntityNotFoundException {
        if ((user.getAccountType().equals(AccountType.PROFESSOR) &&
                user.getCourses().stream().map(Course::getId).noneMatch(id -> id.equals(chapter.getCourse().getId())))
                || (user.getAccountType().equals(AccountType.STUDENT) && !user.inCourse(chapter.getCourse().getId()))) {
            log.error("User {} does not have access to chapter {}", user.getId(), chapter.getId());
            throw new EntityNotFoundException("User " + user.getId() +" does not have access to chapter " + chapter.getId());
        }
    }
}
