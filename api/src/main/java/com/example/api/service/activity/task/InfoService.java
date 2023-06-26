package com.example.api.service.activity.task;

import com.example.api.dto.request.activity.task.create.CreateInfoChapterForm;
import com.example.api.dto.request.activity.task.create.CreateInfoForm;
import com.example.api.dto.request.activity.task.edit.EditInfoForm;
import com.example.api.dto.response.activity.task.InfoResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.model.activity.task.Info;
import com.example.api.model.map.Chapter;
import com.example.api.model.user.User;
import com.example.api.model.util.Url;
import com.example.api.repository.activity.task.InfoRepository;
import com.example.api.repository.map.ChapterRepository;
import com.example.api.repository.user.UserRepository;
import com.example.api.repository.util.UrlRepository;
import com.example.api.security.AuthenticationService;
import com.example.api.service.map.RequirementService;
import com.example.api.service.validator.ChapterValidator;
import com.example.api.service.validator.UserValidator;
import com.example.api.service.validator.activity.ActivityValidator;
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
        userValidator.validateProfessorAccount(professor, email);

        List<Url> imageUrls = form.getImageUrls()
                .stream()
                .map(url -> new Url(null, url))
                .toList();
        urlRepository.saveAll(imageUrls);

        Info info = new Info(
                form,
                professor,
                imageUrls
        );
        info.setRequirements(requirementService.getDefaultRequirements(true));
        infoRepository.save(info);
        chapter.getActivityMap().getInfos().add(info);
    }

    public List<Info> getStudentInfos() {
        return infoRepository.findAll()
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
