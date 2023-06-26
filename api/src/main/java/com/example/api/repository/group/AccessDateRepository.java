package com.example.api.repository.group;

import com.example.api.model.group.AccessDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessDateRepository extends JpaRepository<AccessDate, Long> {

}
