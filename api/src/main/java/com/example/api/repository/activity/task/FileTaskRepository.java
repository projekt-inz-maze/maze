package com.example.api.repository.activity.task;

import com.example.api.model.activity.task.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask, Long> {
    FileTask findFileTaskById(Long id);
    FileTask findFileTaskByTitle(String title);
    boolean existsById(long id);
}
