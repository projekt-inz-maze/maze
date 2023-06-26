package com.example.api.repository.user;

import com.example.api.model.user.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Rank findRankById(Long id);
}
