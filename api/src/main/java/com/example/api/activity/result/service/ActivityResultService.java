package com.example.api.activity.result.service;

import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.result.repository.ActivityResultRepository;
import com.example.api.activity.result.repository.FileTaskResultRepository;
import com.example.api.activity.result.repository.GraphTaskResultRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.course.coursemember.CourseMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityResultService {
    private final GraphTaskResultRepository graphTaskResultRepository;
    private final FileTaskResultRepository fileTaskResultRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final ActivityResultRepository activityResultRepository;

    public List<? extends ActivityResult> getResultsForActivity(Long activityId) {
        return activityResultRepository.findAllByActivity_Id(activityId);
    }

    public long countCompletedActivities(CourseMember member) {
        return graphTaskResultRepository.countAllByMemberAndSendDateMillisNotNull(member)
                + fileTaskResultRepository.countAllByMember(member)
                + surveyResultRepository.countAllByMember(member);
    }
}
