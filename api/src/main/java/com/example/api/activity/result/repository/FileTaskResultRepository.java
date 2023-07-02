package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskResultRepository extends JpaRepository<FileTaskResult, Long> {
    FileTaskResult findFileTaskResultById(Long id);
    FileTaskResult findFileTaskResultByFileTaskAndUser(FileTask fileTask, User user);
    List<FileTaskResult> findAllByUser(User user);
    List<FileTaskResult> findAllByFileTask(FileTask fileTask);
}
