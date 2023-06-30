package com.example.api.map.repository;

import com.example.api.activity.task.model.Activity;
import com.example.api.map.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Chapter findChapterById(Long id);
    Long deleteChapterById(Long id);
    Chapter findChapterByActivityMapContaining(Activity activity);
}
