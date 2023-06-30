package com.example.api.activity.task.dto.response.result.summary.util;

import com.example.api.activity.task.dto.response.result.summary.AverageGradeForChapter;
import com.example.api.group.model.Group;
import com.example.api.util.calculator.GradesCalculator;

import java.util.LinkedList;
import java.util.List;


public class AverageGradeForChapterCreator {
    private String groupName;
    private List<Double> grades;

    public AverageGradeForChapterCreator(Group group) {
        this.groupName = group.getName();
        this.grades = new LinkedList<>();
    }

    public void add(Double grade) {
        grades.add(grade);
    }

    public AverageGradeForChapter create() {
        Double avgGrade = GradesCalculator.getAvg(grades);
        if (avgGrade != null ) avgGrade = GradesCalculator.roundGrade(avgGrade);

        Double medianGrade = GradesCalculator.getMedian(grades);
        if (medianGrade != null) medianGrade = GradesCalculator.roundGrade(medianGrade);
        return new AverageGradeForChapter(groupName, avgGrade, medianGrade);
    }
}
