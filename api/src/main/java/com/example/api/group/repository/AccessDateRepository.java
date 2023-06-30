package com.example.api.group.repository;

import com.example.api.group.model.AccessDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessDateRepository extends JpaRepository<AccessDate, Long> {

}
