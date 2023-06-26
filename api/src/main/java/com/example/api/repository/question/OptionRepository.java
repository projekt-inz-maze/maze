package com.example.api.repository.question;

import com.example.api.model.question.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findOptionById(Long id);
}
