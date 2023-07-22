package com.example.api.user.repository;

import com.example.api.course.model.Course;
import com.example.api.user.model.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Rank findRankById(Long id);
    List<Rank> findAllByCourse(Course course);
}
