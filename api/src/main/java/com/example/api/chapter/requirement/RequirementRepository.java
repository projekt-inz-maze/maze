package com.example.api.chapter.requirement;

import com.example.api.chapter.requirement.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    Requirement findRequirementById(Long id);
}
