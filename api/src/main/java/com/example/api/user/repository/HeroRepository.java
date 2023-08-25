package com.example.api.user.repository;

import com.example.api.course.model.Course;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    Hero findHeroByType(HeroType type);
    Hero findHeroByTypeAndCourse(HeroType type, Course course);
}
