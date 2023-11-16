package com.example.api.activity.result.repository;

import com.example.api.activity.Activity;
import com.example.api.activity.result.model.TaskResult;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskResultRepository extends JpaRepository<TaskResult, Long> {
    List<TaskResult> findAllByActivity(Activity activity);
    TaskResult findByActivity_IdAndMember_User(Long activity, User user);
}
