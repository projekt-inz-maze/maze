package com.example.api.activity.repository.task;

import com.example.api.activity.task.model.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask, Long> {
    FileTask findFileTaskById(Long id);
    FileTask findFileTaskByTitle(String title);
    boolean existsById(long id);
}
