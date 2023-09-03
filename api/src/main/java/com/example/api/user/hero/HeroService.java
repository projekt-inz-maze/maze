package com.example.api.user.hero;

import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.hero.model.*;

import com.example.api.validator.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class HeroService {
    private final HeroRepository heroRepository;
    private final LoggedInUserService userService;
    private final UserValidator userValidator;
    private final CourseService courseService;

    public void updateHero(UpdateHeroForm form) throws WrongUserTypeException, EntityNotFoundException {
        userValidator.validateProfessorAccount(userService.getCurrentUser());
        Hero hero = heroRepository.findHeroByTypeAndCourse(form.getType(), courseService.getCourse(form.getCourseId()));
        Long coolDown = form.getCoolDownMillis();
        if (coolDown != null) {
            hero.setCoolDownTimeMillis(coolDown);
        }
        Double value = form.getValue();
        if (value != null) {
            hero.changeValue(value);
        }
    }

    public void addHeroes(List<Hero> heroes) {
        heroRepository.saveAll(heroes);
    }
}
