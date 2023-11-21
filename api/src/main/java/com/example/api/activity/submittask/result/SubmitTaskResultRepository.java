package com.example.api.activity.submittask.result;

import com.example.api.activity.Activity;
import com.example.api.course.Course;
import com.example.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmitTaskResultRepository  extends JpaRepository<SubmitTaskResult, Long> {
    List<SubmitTaskResult> findAllByMember_CourseIsAndActivity_ProfessorIs(Course course, User professor);
    List<SubmitTaskResult> findAllByActivityAndEvaluatedIs(Activity activity, boolean evaluated);
}
