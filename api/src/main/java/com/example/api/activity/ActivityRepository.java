package com.example.api.activity;

import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
   List<Activity> findAllByProfessor(User from);
}
