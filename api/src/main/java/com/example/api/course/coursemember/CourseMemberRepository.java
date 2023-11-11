package com.example.api.course.coursemember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMemberRepository extends JpaRepository<CourseMember, Long> {
    List<CourseMember> findAllByCourse_Id(Long courseId);
}
