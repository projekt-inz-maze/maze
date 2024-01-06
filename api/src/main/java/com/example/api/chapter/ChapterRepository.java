package com.example.api.chapter;

import com.example.api.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Chapter findChapterById(Long id);
    Long deleteChapterById(Long id);
    List<Chapter> findAllByCourse(Course course);
    List<Chapter> findAllByCourse_Id(Long courseId);
    Chapter findFirstByCourseIsAndNextChapterIsNull(Course course);
}
