package com.example.api.activity.task.graphtask;

import com.example.api.activity.auction.CreateAuctionDTO;
import com.example.api.activity.task.CreateTaskForm;
import com.example.api.activity.ActivityType;
import com.example.api.question.QuestionForm;
import com.example.api.question.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGraphTaskForm extends CreateTaskForm {
    @Schema(required = true) private List<QuestionForm> questions;
    @Schema(required = true) private String timeToSolve;

    public CreateGraphTaskForm(String title,
                               String description,
                               Integer posX,
                               Integer posY,
                               String requiredKnowledge,
                               List<QuestionForm> questions,
                               CreateAuctionDTO auctionDTO,
                               String timeToSolve) {
        super(ActivityType.EXPEDITION, title, description, posX, posY, requiredKnowledge);
        this.questions = questions;
        this.timeToSolve = timeToSolve;
        setAuction(auctionDTO);
    }

    public CreateGraphTaskForm(GraphTask graphTask) {
        super(graphTask);

        HashMap<Long, Integer> mapping = getIdToQuestionNumberMapping(graphTask.getQuestions());
        this.questions = graphTask.getQuestions().stream().map(q->new QuestionForm(q, mapping)).toList();
        SimpleDateFormat timeToSolveFormat = new SimpleDateFormat("HH:mm:ss");
        if (graphTask.getTimeToSolveMillis() != null) {
            this.timeToSolve = timeToSolveFormat.format(new Date(graphTask.getTimeToSolveMillis()));
        }
    }

    private HashMap<Long, Integer> getIdToQuestionNumberMapping(List<Question> questions) {
        HashMap<Long, Integer> mapping = new HashMap<>();
        for (Question q: questions) {
            DFSGraph(q, mapping);
        }
        return mapping;
    }

    private void DFSGraph(Question question, HashMap<Long, Integer> mapping) {
        if (mapping.containsKey(question.getId())) return;
        mapping.put(question.getId(), mapping.size());
        for (Question q: question.getNext()) {
            DFSGraph(q, mapping);
        }
    }
}
