package com.example.api.user.service;

import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.user.dto.request.UpdateHeroForm;
import com.example.api.user.model.hero.Hero;
import com.example.api.user.repository.HeroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HeroService {
    private final HeroRepository heroRepository;
    private final UserService userService;

    public void updateHero(UpdateHeroForm form) throws WrongUserTypeException {
        userService.getCurrentUserAndValidateProfessorAccount();
        Hero hero = heroRepository.findHeroByTypeAndCourseId(form.getType(), form.getCourseId());
        Long coolDown = form.getCoolDownMillis();
        if (coolDown != null) {
            hero.setCoolDownTimeMillis(coolDown);
        }
        Double value = form.getValue();
        if (value != null) {
            hero.changeValue(value);
        }
    }
}
