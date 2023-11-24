package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskResultRepository extends JpaRepository<FileTaskResult, Long> {
    FileTaskResult findFileTaskResultById(Long id);

    @Query("SELECT ftr FROM FileTaskResult ftr WHERE ftr.activity = ?1 AND ftr.member.user = ?2")
    FileTaskResult findFileTaskResultByFileTaskAndUser(Activity fileTask, User user);
    List<FileTaskResult> findAllByMember_UserAndCourse(User user, Course course);
    List<FileTaskResult> findAllByMember(CourseMember member);
    List<FileTaskResult> findAllByActivity(Activity fileTask);
    long countAllByMember(CourseMember member);
    List<FileTaskResult> findAllByMember_CourseIsAndActivity_ProfessorIs(Course course, User professor);
}
