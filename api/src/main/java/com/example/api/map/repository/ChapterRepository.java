package com.example.api.map.repository;

import com.example.api.course.model.Course;
import com.example.api.map.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Chapter findChapterById(Long id);
    Long deleteChapterById(Long id);
    List<Chapter> findAllByCourse(Course course);
    Chapter findFirstByCourseIsAndNextChapterIsNull(Course course);
}
