package com.example.api.user.service;

import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.dto.request.UpdateHeroForm;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.hero.*;
import com.example.api.user.repository.HeroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HeroService {
    private final HeroRepository heroRepository;
    private final UserService userService;
    private final CourseService courseService;

    public void updateHero(UpdateHeroForm form) throws WrongUserTypeException, EntityNotFoundException {
        userService.getCurrentUserAndValidateProfessorAccount();
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

    public Hero createHero(HeroType type, Double value, Long coolDownMillis, Course course) {
        Hero hero = switch (type) {
            case WARRIOR -> new Warrior(type, coolDownMillis, course);
            case WIZARD ->  new Wizard(type, coolDownMillis, course);
            case PRIEST -> new Priest(type, coolDownMillis, course);
            case ROGUE ->  new Rogue(type, coolDownMillis, course);
        };

        if (value != null) {hero.changeValue(value);}
        return hero;
    }

    public void addHeroes(List<Hero> heroes) {
        heroRepository.saveAll(heroes);
    }
}
