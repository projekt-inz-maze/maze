package com.example.api.chapter;

import com.example.api.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Chapter findChapterById(Long id);
    Long deleteChapterById(Long id);
    List<Chapter> findAllByCourse(Course course);
    Chapter findFirstByCourseIsAndNextChapterIsNull(Course course);
}
