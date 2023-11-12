package com.example.api.activity.result.service;

import com.example.api.activity.result.model.TaskResult;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.course.coursemember.CourseMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityResultService {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;

    public List<? extends TaskResult> getResultsForActivity(Long activityId) {
        return Stream.of(graphTaskResultRepository.findAllByGraphTaskId(activityId),
                    fileTaskResultRepository.findAllByFileTaskId(activityId),
                    surveyResultRepository.findAllBySurveyId(activityId)
                )
                .flatMap(Collection::stream)
                .toList();
    }

    public long countCompletedActivities(CourseMember member) {
        return graphTaskResultRepository.countAllByMemberAndSendDateMillisNotNull(member)
                + fileTaskResultRepository.countAllByMember(member)
                + surveyResultRepository.countAllByMember(member);
    }
}
