package com.example.api.course.service;

import com.example.api.course.model.CourseMember;
import com.example.api.course.repository.CourseMemberRepository;
import com.example.api.group.model.Group;
import com.example.api.group.service.GroupService;
import com.example.api.user.model.User;
import com.example.api.user.model.hero.UserHero;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CourseMemberService {
    GroupService groupService;
    CourseMemberRepository repository;

    public void updateGroup(CourseMember member, Group group) {
        log.info("Changing group for user {} from {} to {}", member.getUser(), member.getGroup().getId(), group.getId());
        groupService.removeUser(member, member.getGroup());
        groupService.addUser(member, group);
        member.setGroup(group);
        repository.save(member);
    }

    public CourseMember create(User user, Group group, UserHero userHero) {
        CourseMember cm = new CourseMember(user, group, userHero);
        repository.save(cm);
        return cm;
    }

    public List<CourseMember> getAll(Long courseId) {
        return repository.findAllByCourse_Id(courseId);
    }
}
