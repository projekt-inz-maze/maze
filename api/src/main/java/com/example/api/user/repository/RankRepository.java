package com.example.api.user.repository;

import com.example.api.course.Course;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Rank findRankById(Long id);
    List<Rank> findAllByCourseIs(Course course);
    List<Rank> findAllByCourseIsAndHeroTypeIs(Course course, HeroType heroType);
}
