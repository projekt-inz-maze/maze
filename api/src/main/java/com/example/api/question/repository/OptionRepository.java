package com.example.api.question.repository;

import com.example.api.question.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findOptionById(Long id);
}
