package com.example.api.repository.group;

import com.example.api.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupById(Long id);
    Group findGroupByInvitationCode(String invitationCode);
    Group findGroupByName(String name);
}
