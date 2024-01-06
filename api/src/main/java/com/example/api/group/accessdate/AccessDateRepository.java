package com.example.api.group.accessdate;

import com.example.api.group.accessdate.AccessDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessDateRepository extends JpaRepository<AccessDate, Long> {

}
