package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
    List<TaskResult> findAllByActivity(Activity activity);
    Optional<TaskResult> findByActivityAndMember(Activity auction, CourseMember member);
    TaskResult findByActivity_IdAndMember_User(Long activity, User user);

}
