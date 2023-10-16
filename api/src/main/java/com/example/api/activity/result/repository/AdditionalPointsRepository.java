package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalPointsRepository extends JpaRepository<AdditionalPoints, Long> {
    List<AdditionalPoints> findAllByMember(CourseMember member);

    @Query("SELECT ap FROM AdditionalPoints ap WHERE ap.member.user = ?1 AND ap.course = ?2")
    List<AdditionalPoints> findAllByUserAndCourse(User user, Course course);
}
