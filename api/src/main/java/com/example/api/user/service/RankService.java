package com.example.api.user.service;

import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.user.dto.request.rank.AddRankForm;
import com.example.api.user.dto.request.rank.EditRankForm;
import com.example.api.user.dto.response.rank.CurrentRankResponse;
import com.example.api.user.dto.response.rank.RankResponse;
import com.example.api.user.dto.response.rank.RanksForHeroTypeResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.util.model.Image;
import com.example.api.util.model.ImageType;
import com.example.api.user.repository.RankRepository;
import com.example.api.util.repository.ImageRepository;
import com.example.api.validator.RankValidator;
import com.example.api.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RankService {
    private final RankRepository rankRepository;
    private final ImageRepository imageRepository;
    private final RankValidator rankValidator;
    private final UserValidator userValidator;
    private final CourseService courseService;
    private final UserService userService;
    private final CourseValidator courseValidator;

    public List<RanksForHeroTypeResponse> getAllRanks(Long courseId) throws EntityNotFoundException {
        Course course = courseService.getCourse(courseId);
        courseValidator.validateCurrentUserCanAccess(courseId);

        return getHeroTypeToRanks(course).entrySet().stream().map(e -> new RanksForHeroTypeResponse(
                e.getKey(),
                e.getValue()
                        .stream()
                        .sorted(Comparator.comparingDouble(Rank::getMinPoints))
                        .map(rank -> new RankResponse(
                                rank.getId(),
                                rank.getName(),
                                rank.getMinPoints(),
                                rank.getImage().getFile())
                        )
                        .toList()
                ))
                .toList();
    }

    public Map<HeroType, List<Rank>> getHeroTypeToRanks(Course course) {
        return rankRepository.findAllByCourse(course).stream().collect(Collectors.groupingBy(Rank::getHeroType));
    }

    public void addRank(AddRankForm form) throws RequestValidationException, IOException {
        rankValidator.validateAddRankForm(form);
        courseValidator.validateCurrentUserCanAccess(form.getCourseId());
        Course course = courseService.getCourse(form.getCourseId());
        MultipartFile multipartFile = form.getImage();
        Image image = new Image(form.getName() + " image", multipartFile.getBytes(), ImageType.RANK, course);
        imageRepository.save(image);
        Rank rank = new Rank(
                null,
                form.getType(),
                form.getName(),
                form.getMinPoints(),
                image,
                course
        );
        rankRepository.save(rank);
    }

    public void updateRank(EditRankForm form) throws RequestValidationException, IOException {
        User user = userService.getCurrentUser();
        Long id = form.getRankId();
        Rank rank = rankRepository.findRankById(id);
        courseValidator.validateCourseOwner(rank.getCourse(), user);
        rankValidator.validateEditRankForm(form, rank, id);

        if (form.getName() != null) {
            rank.setName(form.getName());
        }
        if (form.getMinPoints() != null) {
            rank.setMinPoints(form.getMinPoints());
        }
        if (form.getImage() != null) {
            Image image = rank.getImage();
            image.setFile(form.getImage().getBytes());
        }
        if (form.getType() != null) {
            rank.setHeroType(form.getType());
        }
    }

    public CurrentRankResponse getCurrentRank(Long courseId) throws WrongUserTypeException, EntityNotFoundException {
        User user = userService.getCurrentUserAndValidateStudentAccount();
        return getCurrentRank(user, courseId);
    }

    public CurrentRankResponse getCurrentRank(User user, Long courseId) throws EntityNotFoundException {
        courseValidator.validateUserCanAccess(user, courseId);
        Course course = courseService.getCourse(courseId);

        double points = user.getPoints();
        HeroType heroType = user.getHeroType();
        List<Rank> ranks = getSortedRanksForHeroType(heroType, course);
        Rank currentRank = getCurrentRank(ranks, points);
        if (currentRank == null) {
            if (ranks.size() == 0) {
                return new CurrentRankResponse(null, null, null, points);
            } else {
                return new CurrentRankResponse(null, null, new RankResponse(ranks.get(0)), points);
            }
        }

        Rank previousRank = null;
        Rank nextRank = null;

        int idx = ranks.indexOf(currentRank);
        if (idx == 0 && ranks.size() > 1) {
            nextRank = ranks.get(1);
        } else if (idx == ranks.size() - 1 && ranks.size() > 1) {
            previousRank = ranks.get(idx - 1);
        } else if (ranks.size() > 2) {
            previousRank = ranks.get(idx - 1);
            nextRank = ranks.get(idx + 1);
        }

        return new CurrentRankResponse(
                previousRank == null ? null : new RankResponse(previousRank),
                new RankResponse(currentRank),
                nextRank == null ? null : new RankResponse(nextRank),
                points
        );
    }

    private Rank getCurrentRank(List<Rank> ranks, double points) {
        Rank currRank = null;
        for (Rank rank: ranks) {
            if (rank.getMinPoints() <= points) {
                currRank = rank;
            }
        }
        return currRank;
    }

    public List<Rank> getSortedRanksForHeroType(HeroType heroType, Course course) throws EntityNotFoundException {
        return getHeroTypeToRanks(course).get(heroType)
                .stream()
                .sorted(Comparator.comparingDouble(Rank::getMinPoints))
                .toList();
    }

    public Rank getCurrentRankB(User user, Course course) throws EntityNotFoundException {
        List<Rank> ranks = getSortedRanksForHeroType(user.getHeroType(), course);
        return getCurrentRank(ranks, user.getPoints());
    }

    public void deleteRank(Long id) throws RequestValidationException {
        User owner = userService.getCurrentUser();
        log.info("Deleting rank with id {}", id);
        Rank rank = rankRepository.findRankById(id);
        rankValidator.validateRankIsNotNull(rank, id);
        courseValidator.validateCourseOwner(rank.getCourse(), owner);
        rankRepository.delete(rank);
    }
}
