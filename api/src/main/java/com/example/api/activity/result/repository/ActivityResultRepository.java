package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.course.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityResultRepository extends JpaRepository<ActivityResult, Long> {
    List<ActivityResult> findAllByActivity(Activity activity);
    List<ActivityResult> findAllByActivity_Id(Long id);
    Optional<ActivityResult> findByActivity_IdAndMember_User(Long activity, User user);
    List<ActivityResult> findAllByMember_CourseAndMember_User(Course course, User user);
}
