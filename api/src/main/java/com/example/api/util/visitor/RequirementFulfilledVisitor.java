package com.example.api.util.visitor;

import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.course.model.Course;
import com.example.api.map.model.requirement.*;
import com.example.api.security.AuthenticationService;
import com.example.api.user.model.User;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementFulfilledVisitor {
    private final UserService userService;
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final AuthenticationService authService;

    public boolean visitDateFromRequirement(DateFromRequirement requirement) {
        if (!requirement.getSelected()) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis() > requirement.getDateFromMillis();
    }

    public boolean visitDateToRequirement(DateToRequirement requirement) {
        if (!requirement.getSelected()) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis() < requirement.getDateToMillis();
    }

    public boolean visitFileTasksRequirement(FileTasksRequirement requirement, Course course) {
        if (!requirement.getSelected()) {
            return true;
        }
        User student = authService.getCurrentUser();
        List<FileTask> fileTasks = fileTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .map(FileTaskResult::getFileTask)
                .toList();
        return new HashSet<>(fileTasks).containsAll(requirement.getFinishedFileTasks());
    }

    public boolean visitGraphTasksRequirement(GraphTasksRequirement requirement, Course course) {
        if (!requirement.getSelected()) {
            return true;
        }
        User student = authService.getCurrentUser();
        List<GraphTask> graphTasks = graphTaskResultRepository.findAllByUserAndCourse(student, course)
                .stream()
                .map(GraphTaskResult::getGraphTask)
                .toList();
        return new HashSet<>(graphTasks).containsAll(requirement.getFinishedGraphTasks());
    }

    public boolean visitGroupsRequirement(GroupsRequirement requirement, Course course) {
        if (!requirement.getSelected()) {
            return true;
        }
        User student = authService.getCurrentUser();
        return student.getCourseMember(course.getId()).map(cm -> requirement.getAllowedGroups().contains(cm.getGroup())).orElse(false);

    }

    public boolean visitMinPointsRequirement(MinPointsRequirement requirement) {
        if (!requirement.getSelected()) {
            return true;
        }
        User student = authService.getCurrentUser();
        return student.getPoints() >= requirement.getMinPoints();
    }

    public boolean visitStudentsRequirements(StudentsRequirements requirement) {
        if (!requirement.getSelected()) {
            return true;
        }
        User student = authService.getCurrentUser();
        return requirement.getAllowedStudents().contains(student);
    }
}
