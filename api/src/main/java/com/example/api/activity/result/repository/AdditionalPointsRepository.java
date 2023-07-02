package com.example.api.activity.result.repository;

import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalPointsRepository extends JpaRepository<AdditionalPoints, Long> {
    List<AdditionalPoints> findAllByUser(User user);
}
