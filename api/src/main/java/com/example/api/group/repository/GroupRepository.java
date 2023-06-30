package com.example.api.group.repository;

import com.example.api.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupById(Long id);
    Group findGroupByInvitationCode(String invitationCode);
    Group findGroupByName(String name);
}
