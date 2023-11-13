package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphTaskResultRepository extends JpaRepository<GraphTaskResult, Long> {
    GraphTaskResult findGraphTaskResultById(Long id);

    @Query("SELECT gtr FROM GraphTaskResult gtr WHERE gtr.activity = ?1 AND gtr.member.user = ?2")
    GraphTaskResult findGraphTaskResultByGraphTaskAndUser(GraphTask task, User user);

    @Query("SELECT gtr FROM GraphTaskResult gtr WHERE gtr.activity.id = ?1 AND gtr.member.user = ?2")
    GraphTaskResult findGraphTaskResultByGraphTaskIdAndUser(Long taskId, User user);

    boolean existsByActivityAndMember(Activity task, CourseMember member);

    List<GraphTaskResult> findAllByMember(CourseMember courseMember);

    @Query("SELECT gtr FROM GraphTaskResult gtr WHERE gtr.member.user = ?1 AND gtr.course = ?2")

    List<GraphTaskResult> findAllByUserAndCourse(User user, Course course);

    List<GraphTaskResult> findAllByActivity(Activity graphTask);

    @Query("SELECT gtr FROM GraphTaskResult gtr WHERE gtr.activity.id = ?1")
    List<GraphTaskResult> findAllByGraphTaskId(Long graphTaskId);

    Long countAllByMemberAndSendDateMillisNotNull(CourseMember member);
}
