package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskResultRepository extends JpaRepository<FileTaskResult, Long> {
    FileTaskResult findFileTaskResultById(Long id);

    @Query("SELECT ftr FROM FileTaskResult ftr WHERE ftr.fileTask = ?1 AND ftr.member.user = ?2")
    FileTaskResult findFileTaskResultByFileTaskAndUser(FileTask fileTask, User user);
    List<FileTaskResult> findAllByMember(CourseMember member);
    List<FileTaskResult> findAllByMember_UserAndCourse(User user, Course course);
    List<FileTaskResult> findAllByFileTask(FileTask fileTask);
    @Query("SELECT ftr FROM FileTaskResult ftr WHERE ftr.fileTask.id = ?1")
    List<FileTaskResult> findAllByFileTaskId(Long fileTaskId);

    long countAllByMember(CourseMember member);
}
