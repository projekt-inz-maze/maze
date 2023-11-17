package com.example.api.activity.result.service.util;

import com.example.api.activity.auction.Auction;
import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.task.dto.response.result.ScaleActivityStatistics;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.model.ActivityResult;
import com.example.api.activity.Activity;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.Task;
import com.example.api.util.csv.PointsToGradeMapper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ScaleActivityStatisticsCreator {
    private Double maxPoints;
    private final PointsToGradeMapper gradeMapper = new PointsToGradeMapper();
    private final HashMap<Double, ScaleActivityStatistics> statistics = new HashMap<>();

    public ScaleActivityStatisticsCreator(Activity activity) {
        switch (activity.getActivityType()) {
            case SURVEY -> initSurvey((Survey) activity);
            case TASK, EXPEDITION -> initTask((Task) activity);
            case AUCTION -> initAuction((Auction) activity);
            default -> throw new IllegalStateException("Illegal activity type");
        }
    }

    public void initTask(Task task) {
        this.maxPoints = task.getMaxPoints();

        Stream.of(5.0, 4.5, 4.0, 3.5, 3.0, 2.0)
                .forEach(grade -> statistics.put(grade, new ScaleActivityStatistics(grade)));
    }

    public void initSurvey(Survey survey) {
        Stream.of(5.0, 4.0, 3.0, 2.0, 1.0)
                .forEach(grade -> statistics.put(grade, new ScaleActivityStatistics(grade)));
    }

    public void initAuction(Auction auction) {
        this.maxPoints = auction.getMaxPoints();
    }

    public void add(ActivityResult activityResult) {
        switch (activityResult.getActivity().getActivityType()) {
            case SURVEY:
                addSurvey((SurveyResult) activityResult);
                break;
            case TASK:
            case EXPEDITION:
                addTask(activityResult);
                break;
            case AUCTION:
                addAuction((Bid) activityResult);
                break;
            default:
                throw new IllegalStateException("Illegal activity type");
        }
    }


    public void addAll(List<? extends ActivityResult> taskResults) {
        taskResults.forEach(this::add);
    }
    public void addTask(ActivityResult activityResult) {
        Double grade = gradeMapper.getGrade(activityResult.getPoints(), maxPoints);
        statistics.get(grade).incrementResults();
    }

    public void addSurvey(SurveyResult surveyResult) {
        if (surveyResult.getRate() != null) {
            statistics.get(Double.valueOf(surveyResult.getRate())).incrementResults();
        }
    }

    public void addAuction(Bid bid) {
        statistics.putIfAbsent(bid.getPoints(), new ScaleActivityStatistics(bid.getPoints()));
        statistics.get(bid.getPoints()).incrementResults();
    }

    public List<ScaleActivityStatistics> create() {
        return statistics.values()
                .stream()
                .sorted(Comparator.comparingDouble(ScaleActivityStatistics::getGrade).reversed())
                .toList();
    }
}
