package com.example.api.activity.task.service;

import com.example.api.activity.task.dto.request.create.CreateInfoChapterForm;
import com.example.api.activity.task.dto.request.create.CreateInfoForm;
import com.example.api.activity.task.dto.request.edit.EditInfoForm;
import com.example.api.activity.task.dto.response.InfoResponse;
import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.task.model.Info;
import com.example.api.map.model.Chapter;
import com.example.api.user.model.User;
import com.example.api.util.model.Url;
import com.example.api.activity.task.repository.InfoRepository;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.user.repository.UserRepository;
import com.example.api.util.repository.UrlRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.map.service.RequirementService;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import com.example.api.activity.validator.ActivityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InfoService {
    private final InfoRepository infoRepository;
    private final ChapterRepository chapterRepository;
    private final ActivityValidator activityValidator;
    private final AuthenticationService authService;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UrlRepository urlRepository;
    private final RequirementService requirementService;
    private final ChapterValidator chapterValidator;

    public Info saveInfo(Info info){
        return infoRepository.save(info);
    }

    public InfoResponse getInfo(Long id) throws EntityNotFoundException {
        log.info("Fetching info");
        Info info = infoRepository.findInfoById(id);
        activityValidator.validateActivityIsNotNull(info, id);
        List<String> urls = info.getImageUrls()
                .stream()
                .map(Url::getUrl)
                .toList();
        return new InfoResponse(info.getTitle(), info.getDescription(), urls, info.getContent());
    }

    public void createInfo(CreateInfoChapterForm chapterForm) throws RequestValidationException {
        log.info("Starting the creation of info");
        CreateInfoForm form = chapterForm.getForm();
        Chapter chapter = chapterRepository.findChapterById(chapterForm.getChapterId());

        chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
        activityValidator.validateCreateInfoForm(form);
        activityValidator.validateActivityPosition(form, chapter);

        String email = authService.getAuthentication().getName();
        User professor = userRepository.findUserByEmail(email);
        userValidator.validateProfessorAccount(professor);

        List<Url> imageUrls = form.getImageUrls()
                .stream()
                .map(url -> new Url(null, url))
                .toList();
        urlRepository.saveAll(imageUrls);

        Info info = new Info(
                form,
                professor,
                imageUrls,
                null
        );
        info.setRequirements(requirementService.getDefaultRequirements(true));
        infoRepository.save(info);
        chapter.getActivityMap().getInfos().add(info);
    }

    public List<Info> getStudentInfos(Course course) {
        return infoRepository.findAllByCourseAndIsBlockedFalse(course)
                .stream()
                .filter(info -> !info.getIsBlocked())
                .toList();
    }

    public void editInfo(Info info, EditInfoForm form) {
        CreateInfoForm infoForm = (CreateInfoForm) form.getActivityBody();
        info.setContent(infoForm.getInfoContent());
        editImageUrls(info, infoForm.getImageUrls());
    }

    private void editImageUrls(Info info, List<String> newUrlsString) {
        List<Url> remainingUrls = info.getImageUrls()
                .stream()
                .filter(oldUrl -> newUrlsString.stream().anyMatch(newUrl -> oldUrl.getUrl().equals(newUrl)))
                .toList();
        List<Url> newUrls = newUrlsString
                .stream()
                .filter(newUrlString -> remainingUrls.stream().noneMatch(remainingUrl -> remainingUrl.getUrl().equals(newUrlString)))
                .map(newUrlString -> {
                    Url newUrl = new Url();
                    newUrl.setUrl(newUrlString);
                    return newUrl;
                })
                .toList();
        urlRepository.saveAll(newUrls);
        List<Url> updatedUrls = new LinkedList<>();
        updatedUrls.addAll(remainingUrls);
        updatedUrls.addAll(newUrls);
        info.getImageUrls().clear();
        info.getImageUrls().addAll(updatedUrls);

    }


}
