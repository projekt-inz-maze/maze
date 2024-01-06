package com.example.api.user.hero;

import com.example.api.course.Course;
import com.example.api.user.hero.model.*;
import org.springframework.stereotype.Component;

@Component
public class HeroFactory {
    public Hero getHero(HeroType type, Double value, Long coolDownMillis, Course course) {
        Hero hero = switch (type) {
            case WARRIOR -> new Warrior(type, coolDownMillis, course);
            case WIZARD ->  new Wizard(type, coolDownMillis, course);
            case PRIEST -> new Priest(type, coolDownMillis, course);
            case ROGUE ->  new Rogue(type, coolDownMillis, course);
        };

        if (value != null) {hero.changeValue(value);}
        return hero;
    }
}
