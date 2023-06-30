package com.example.api.map.repository;

import com.example.api.map.model.requirement.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    Requirement findRequirementById(Long id);
}
