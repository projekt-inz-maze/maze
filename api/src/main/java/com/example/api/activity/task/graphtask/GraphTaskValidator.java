package com.example.api.activity.task.graphtask;

import com.example.api.question.option.OptionForm;
import com.example.api.question.QuestionForm;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.ExceptionMessage;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.Difficulty;
import com.example.api.question.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class GraphTaskValidator {
    public void validateGraphTaskIsNotNull(GraphTask graphTask, Long id) throws EntityNotFoundException {
        if(graphTask == null) {
            log.error("GraphTask with id {} not found in database", id);
            throw new EntityNotFoundException("GraphTask with id" + id + " not found in database");
        }
    }

    public void validateCreateGraphTaskFormFields(CreateGraphTaskForm form) throws RequestValidationException {
        if (Stream.of(form.getTitle(), form.getDescription(), form.getPosX(), form.getPosY(), form.getTaskContent(),
                form.getQuestions(), form.getTimeToSolve()).anyMatch(Objects::isNull)) {
            log.error("All CreateGraphTaskForm fields cannot be null");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FORM_FIELDS_NOT_NULL);
        }
        List<QuestionForm> questionForms = form.getQuestions();
        if(questionForms.size() < 2) {
            log.error("There must be at least two questions in CreateGraphTaskForm (first & last)");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_QUESTIONS_SIZE);
        }
        for (QuestionForm questionForm: questionForms) {
            validateQuestionForm(questionForm);
        }
        List<Integer> nums = questionForms.stream()
                .map(QuestionForm::getQuestionNum)
                .toList();
        if (nums.get(0) != 0) {
            log.error("First question must have index 0");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_QUESTIONS_FIRST_INDEX);
        }
    }

    private void validateQuestionForm(QuestionForm questionForm) throws RequestValidationException {
        if (questionForm.getQuestionNum() == null) {
            log.error("All questions must contain questionNum");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_QUESTIONS_NUM);
        }
        if (questionForm.getQuestionNum() != 0) {
            if (Stream.of(questionForm.getQuestionType(), questionForm.getContent(), questionForm.getHint(), questionForm.getDifficulty(),
                    questionForm.getPoints(), questionForm.getNextQuestions()).anyMatch(Objects::isNull)) {
                log.error("QuestionType, content, hint, difficulty, points, nextQuestions cannot be null");
                throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_REQ);
            }
            QuestionType type = getQuestionTypeFromString(questionForm.getQuestionType());
            switch (type) {
                case OPENED -> {
                    if (questionForm.getAnswers() != null && !questionForm.getAnswers().isEmpty()) {
                        log.error("Answers for question with type OPENED must be null or empty array");
                        throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_ANSWERS_OPENED);
                    }
                    if (questionForm.getAnswerForOpenedQuestion() == null ||
                            questionForm.getAnswerForOpenedQuestion().isEmpty()) {
                        log.error("Correct answer for question with type OPENED cannot be null or empty string");
                        throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_ANSWER_OPENED);
                    }
                }
                case SINGLE_CHOICE -> {
                    checkChoiceAnswer(questionForm);
                    List<OptionForm> optionForms = questionForm.getAnswers();
                    if (optionForms.stream().filter(OptionForm::getIsCorrect).count() != 1) {
                        log.error("Answers for question with type SINGLE_CHOICE must contain ONE correct answer");
                        throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_ANSWER_SINGLE);
                    }
                    for (OptionForm optionForm: optionForms){
                        checkOptionForm(optionForm);
                    }
                }
                case MULTIPLE_CHOICE -> {
                    checkChoiceAnswer(questionForm);
                    List<OptionForm> optionForms = questionForm.getAnswers();
                    for (OptionForm optionForm: optionForms){
                        checkOptionForm(optionForm);
                    }
                }
            }
        } else {
            if (questionForm.getNextQuestions() == null) {
                log.error("NextQuestions cannot be null for first question");
                throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_QUESTIONS_FIRST);
            }
        }
    }

    private void checkChoiceAnswer(QuestionForm questionForm) throws RequestValidationException {
        if (questionForm.getAnswers() == null || questionForm.getAnswers().size() < 2) {
            log.error("Answers for question with type SINGLE_CHOICE or MULTIPLE_CHOICE cannot be null and must contain at least 2 elements");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_ANSWERS_SINGLE_MULTIPLE);
        }
        if (questionForm.getAnswerForOpenedQuestion() != null && !questionForm.getAnswerForOpenedQuestion().equals("")) {
            log.error("AnswerForOpenedQuestion for question type SINGLE_CHOICE or MULTIPLE_CHOICE must be null or empty string");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_FIELDS_ANSWER_SINGLE_MULTIPLE);
        }
    }

    private void checkOptionForm(OptionForm optionForm) throws RequestValidationException {
        if (Stream.of(optionForm.getContent(), optionForm.getIsCorrect()).anyMatch(Objects::isNull)) {
            log.error("Option form cannot have null fields");
            throw new RequestValidationException(ExceptionMessage.OPTION_FORM_FIELDS);
        }
    }


    public QuestionType getQuestionTypeFromString(String questionType) throws RequestValidationException {
        try {
            return QuestionType.valueOf(questionType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.error("Invalid questionType. [OPENED / SINGLE_CHOICE / MULTIPLE_CHOICE]");
            throw new RequestValidationException(ExceptionMessage.INVALID_QUESTION_TYPE);
        }
    }

    public Difficulty getDifficultyFromString(String difficulty) throws RequestValidationException {
        try {
            return Difficulty.valueOf(difficulty.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.error("Invalid difficulty. [EASY / MEDIUM / HARD]");
            throw new RequestValidationException(ExceptionMessage.INVALID_DIFFICULTY);
        }
    }

    public void validateGraphTaskTitle(String title, List<GraphTask> graphTasks) throws RequestValidationException {
        int idx = title.indexOf(";");
        if (idx != -1) {
            log.error("Title cannot have a semicolon!");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_TITLE_CONTAINS_SEMICOLON);
        }
        if (graphTasks.stream().anyMatch(graphTask -> graphTask.getTitle().equals(title))) {
            log.error("Graph task has to have unique title");
            throw new RequestValidationException(ExceptionMessage.GRAPH_TASK_TITLE_NOT_UNIQUE);
        }
    }
}
