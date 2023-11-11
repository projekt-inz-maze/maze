package com.example.api.user.hero;

import com.example.api.course.Course;
import com.example.api.user.hero.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    Hero findHeroByTypeAndCourse(HeroType type, Course course);
}
