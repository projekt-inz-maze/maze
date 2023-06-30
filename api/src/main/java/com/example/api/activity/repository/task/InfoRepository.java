package com.example.api.activity.repository.task;

import com.example.api.activity.task.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    Info findInfoById(Long id);
    boolean existsById(long id);
}
