package com.example.api.chapter.requirement;

import com.example.api.activity.auction.Auction;
import com.example.api.activity.task.RequirementForm;

import com.example.api.chapter.requirement.model.*;
import com.example.api.course.Course;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.user.model.User;
import com.example.api.validator.MapValidator;
import com.example.api.util.message.MessageManager;
import com.example.api.util.visitor.RequirementFulfilledVisitor;
import com.example.api.util.visitor.RequirementValueVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final RequirementFulfilledVisitor requirementFulfilledVisitor;
    private final RequirementValueVisitor requirementValueVisitor;
    private final MapValidator mapValidator;

    public List<Requirement> getDefaultRequirements(boolean forActivity) {
        Requirement dateFromRequirement = new DateFromRequirement(
                forActivity ? MessageManager.DATE_FROM_REQ_NAME : MessageManager.DATE_FROM_REQ_NAME_CHAPTER,
                false,
                null
        );

        Requirement dateToRequirement = new DateToRequirement(
                forActivity ? MessageManager.DATE_TO_REQ_NAME : MessageManager.DATE_TO_REQ_NAME_CHAPTER,
                false,
                null
        );

        Requirement fileTasksRequirement = new FileTasksRequirement(
                MessageManager.FILE_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        Requirement graphTasksRequirement = new GraphTasksRequirement(
                MessageManager.GRAPH_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );

        Requirement groupsRequirement = new GroupsRequirement(
                forActivity ? MessageManager.GROUPS_REQ_NAME : MessageManager.GROUPS_REQ_NAME_CHAPTER,
                false,
                new LinkedList<>()
        );

        Requirement minPointsRequirement = new MinPointsRequirement(
                MessageManager.MIN_POINTS_REQ_NAME,
                false,
                null
        );

        Requirement studentsRequirements = new StudentsRequirement(
                forActivity ? MessageManager.STUDENTS_REQ_NAME : MessageManager.STUDENTS_REQ_NAME_CHAPTER,
                false,
                new LinkedList<>()
        );

        List<Requirement> requirements = List.of(
                dateFromRequirement,
                dateToRequirement,
                minPointsRequirement,
                groupsRequirement,
                studentsRequirements,
                graphTasksRequirement,
                fileTasksRequirement
        );

        requirementRepository.saveAll(requirements);
        return requirements;
    }

    public boolean areRequirementsFulfilled(List<Requirement> requirements, Course course) {
        return requirements.stream()
                .allMatch(requirement -> requirement.isFulfilled(requirementFulfilledVisitor, course));
    }

    public void updateRequirements(List<RequirementForm> forms) throws RequestValidationException {
        for (RequirementForm requirementForm: forms) {
            Requirement requirement = requirementRepository.findRequirementById(requirementForm.getId());
            mapValidator.validateRequirementIsNotNull(requirement, requirementForm.getId());

            requirement.setSelected(requirementForm.getSelected());
            requirement.setValue(requirementValueVisitor, requirementForm.getValue());
        }
    }

    public List<Requirement> requirementsForAuctionTask(Auction auction, User winner) {
        Requirement dateFromRequirement = new DateFromRequirement(
                MessageManager.DATE_FROM_REQ_NAME,
                false,
                null
        );

        Requirement dateToRequirement = new DateToRequirement(
                MessageManager.DATE_TO_REQ_NAME,
                false,
                null
        );

        Requirement fileTasksRequirement = new FileTasksRequirement(
                MessageManager.FILE_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        Requirement graphTasksRequirement = new GraphTasksRequirement(
                MessageManager.GRAPH_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );

        Requirement groupsRequirement = new GroupsRequirement(
                MessageManager.GROUPS_REQ_NAME,
                false,
                new LinkedList<>()
        );

        Requirement minPointsRequirement = new MinPointsRequirement(
                MessageManager.MIN_POINTS_REQ_NAME,
                false,
                null
        );

        Requirement studentsRequirements =  new StudentsRequirement(auction.getTitle(), true, List.of(winner));

        List<Requirement> requirements = List.of(
                dateFromRequirement,
                dateToRequirement,
                minPointsRequirement,
                groupsRequirement,
                studentsRequirements,
                graphTasksRequirement,
                fileTasksRequirement
        );

        requirementRepository.saveAll(requirements);
        return requirements;
    }
}
