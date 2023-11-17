package com.example.api.activity;

import com.example.api.activity.task.graphtask.GraphTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
