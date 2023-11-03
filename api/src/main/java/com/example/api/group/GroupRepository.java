package com.example.api.group;

import com.example.api.course.model.Course;
import com.example.api.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findGroupById(Long id);
    Group findGroupByInvitationCode(String invitationCode);
    Group findGroupByName(String name);
    List<Group> findAllByCourse(Course course);
}
