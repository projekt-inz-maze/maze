package com.example.api.activity.task.graphtask;

import com.example.api.question.create.OptionForm;
import com.example.api.question.create.QuestionForm;
import com.example.api.activity.validator.ActivityValidator;
import com.example.api.course.model.Course;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.map.model.Chapter;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.map.service.RequirementService;
import com.example.api.question.model.Difficulty;
import com.example.api.question.model.Option;
import com.example.api.question.model.Question;
import com.example.api.question.model.QuestionType;
import com.example.api.question.repository.OptionRepository;
import com.example.api.question.repository.QuestionRepository;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.User;
import com.example.api.util.calculator.TimeParser;
import com.example.api.util.message.MessageManager;
import com.example.api.validator.ChapterValidator;
import com.example.api.validator.UserValidator;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/graph")
@SecurityRequirement(name = "JWT_AUTH")
public class GraphTaskController {
    private final GraphTaskService graphTaskService;

    @GetMapping
    ResponseEntity<GraphTaskResponse> getGraphTaskById(@RequestParam Long id) throws EntityNotFoundException {
        return ResponseEntity.ok().body(graphTaskService.getGraphTaskById(id));
    }

    @GetMapping("/create")
    public ResponseEntity<CreateGraphTaskForm> getExampleGraphTaskForm() {
        List<OptionForm> optionForms1 = List.of(
                new OptionForm(MessageManager.ANS_1, true),
                new OptionForm(MessageManager.ANS_2, false),
                new OptionForm(MessageManager.ANS_3, false));
        List<OptionForm> optionForms2 = List.of(
                new OptionForm(MessageManager.ANS_4, true),
                new OptionForm(MessageManager.ANS_5, true),
                new OptionForm(MessageManager.ANS_6, true),
                new OptionForm(MessageManager.ANS_7, false));
        List<QuestionForm> questionForms = List.of(
                new QuestionForm(0, List.of(1, 2)),
                new QuestionForm(1, "OPENED", MessageManager.QUESTION_1,
                        MessageManager.HINT_1, "EASY", List.of(), 10.0, List.of(3), "1410"),
                new QuestionForm(2, "SINGLE_CHOICE", MessageManager.QUESTION_2,
                        MessageManager.HINT_2, "MEDIUM", optionForms1, 20.0, List.of(3), ""),
                new QuestionForm(3, "MULTIPLE_CHOICE", MessageManager.QUESTION_3,
                        MessageManager.HINT_3, "HARD", optionForms2, 30.0, List.of(), "")
        );
        CreateGraphTaskForm form = new CreateGraphTaskForm(
                MessageManager.TITLE,
                MessageManager.DESC,
                3,
                4,
                MessageManager.REQ_KNOWLEDGE,
                questionForms,
                "00:30:00"
        );
        return ResponseEntity.ok().body(form);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGraphTask(@RequestBody CreateGraphTaskChapterForm form)
            throws RequestValidationException, ParseException {
        graphTaskService.createGraphTask(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/map")
    public ResponseEntity<List<GraphNode>> getGraphMap(@RequestParam Long graphTaskID) throws EntityNotFoundException {
        return ResponseEntity.ok().body(graphTaskService.getGraphMap(graphTaskID));
    }

    @Service
    @RequiredArgsConstructor
    @Slf4j
    @Transactional
    public static class GraphTaskService {
        private final GraphTaskRepository graphTaskRepository;
        private final ActivityValidator activityValidator;
        private final LoggedInUserService authService;
        private final OptionRepository optionRepository;
        private final QuestionRepository questionRepository;
        private final ChapterRepository chapterRepository;
        private final UserValidator userValidator;
        private final TimeParser timeParser;
        private final RequirementService requirementService;
        private final ChapterValidator chapterValidator;
        private final GraphTaskValidator graphTaskValidator;

        public GraphTask saveGraphTask(GraphTask graphTask) {
            return graphTaskRepository.save(graphTask);
        }

        public GraphTaskResponse getGraphTaskById(Long id) throws EntityNotFoundException {
            log.info("Fetching graph task with id {}", id);
            GraphTask graphTask = graphTaskRepository.findGraphTaskById(id);
            graphTaskValidator.validateGraphTaskIsNotNull(graphTask, id);
            return new GraphTaskResponse(graphTask);
        }

        public void createGraphTask(CreateGraphTaskChapterForm chapterForm) throws ParseException, RequestValidationException {
            log.info("Starting creation of new GraphTask");
            CreateGraphTaskForm form = chapterForm.getForm();
            Chapter chapter = chapterRepository.findChapterById(chapterForm.getChapterId());

            chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
            activityValidator.validateCreateGraphTaskFormFields(form);
            activityValidator.validateActivityPosition(form, chapter);

            List<GraphTask> graphTasks = graphTaskRepository.findAll();
            activityValidator.validateGraphTaskTitle(form.getTitle(), graphTasks);

            SimpleDateFormat timeToSolveFormat = new SimpleDateFormat("HH:mm:ss");
            long timeToSolveMillis = timeParser.parseAndGetTimeMillisFromHour(timeToSolveFormat, form.getTimeToSolve());

            User professor = authService.getCurrentUser();
            userValidator.validateProfessorAccount(professor);
            List<Question> questions = questionFormsToQuestions(form.getQuestions());
            questionRepository.saveAll(questions);

            double maxPoints = calculateMaxPoints(questions.get(0), 0);

            GraphTask graphTask = new GraphTask(form,
                    professor,
                    questions,
                    timeToSolveMillis,
                    maxPoints,
                    null);
            graphTask.setRequirements(requirementService.getDefaultRequirements(true));
            graphTaskRepository.save(graphTask);

            chapterValidator.validateChapterIsNotNull(chapter, chapterForm.getChapterId());
            chapter.getActivityMap().getGraphTasks().add(graphTask);
        }

        public List<GraphTask> getStudentGraphTasks(Course course) {
            return graphTaskRepository.findAllByCourseAndIsBlockedFalse(course);
        }

        private List<Question> questionFormsToQuestions(List<QuestionForm> questionForms) throws RequestValidationException {
            Map<Integer, Question> numToQuestion = new HashMap<>();
            for (QuestionForm questionForm: questionForms) {
                if(questionForm.getQuestionType() == null) {
                    numToQuestion.put(questionForm.getQuestionNum(), new Question());
                } else {
                    QuestionType type = activityValidator.getQuestionTypeFromString(questionForm.getQuestionType());
                    Difficulty difficulty = activityValidator.getDifficultyFromString(questionForm.getDifficulty());
                    switch (type) {
                        case OPENED -> numToQuestion.put(questionForm.getQuestionNum(),
                                new Question(
                                        type,
                                        questionForm.getContent(),
                                        questionForm.getHint(),
                                        difficulty,
                                        questionForm.getPoints(),
                                        questionForm.getAnswerForOpenedQuestion()));
                        case SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                            Question question = new Question(
                                    type,
                                    questionForm.getContent(),
                                    questionForm.getHint(),
                                    difficulty,
                                    questionForm.getPoints());
                            List<OptionForm> optionForms = questionForm.getAnswers();
                            List<Option> options = optionForms.stream()
                                    .map(optionForm -> new Option(optionForm, question))
                                    .toList();
                            options.forEach(option -> option.setQuestion(question));
                            optionRepository.saveAll(options);
                            question.setOptions(options);
                            numToQuestion.put(questionForm.getQuestionNum(), question);
                        }
                    }
                }
            }
            List<Question> questions = new LinkedList<>();
            for (QuestionForm questionForm: questionForms) {
                Question question = numToQuestion.get(questionForm.getQuestionNum());
                List<Integer> nextQuestionsNum = questionForm.getNextQuestions();
                nextQuestionsNum.forEach(num -> {
                    Question nextQuestion = numToQuestion.get(num);
                    question.getNext().add(nextQuestion);
                });
                questions.add(question);
            }
            return questions;
        }

        private double calculateMaxPoints(Question question, double maxPoints) {
            List<Question> nextQuestions = question.getNext();
            if (nextQuestions.isEmpty()) {
                return maxPoints;
            }
            List<Double> points = new LinkedList<>();
            for (Question nextQuestion: nextQuestions) {
                points.add(calculateMaxPoints(nextQuestion, maxPoints + nextQuestion.getPoints()));
            }
            return points.stream().max(Double::compareTo).get();
        }

        public void editGraphTask(GraphTask graphTask, EditGraphTaskForm form) throws ParseException, RequestValidationException {
            CreateGraphTaskForm graphTaskForm = (CreateGraphTaskForm) form.getActivityBody();
            graphTask.setRequiredKnowledge(graphTaskForm.getRequiredKnowledge());

            if (graphTaskForm.getTimeToSolve() == null) {
                graphTask.setTimeToSolveMillis(null);
            }
            else {
                SimpleDateFormat timeToSolveFormat = new SimpleDateFormat("HH:mm:ss");
                Long timeToSolveMillis = timeToSolveFormat.parse(graphTaskForm.getTimeToSolve()).getTime();
                graphTask.setTimeToSolveMillis(timeToSolveMillis);
            }

            // Checking if there was any update on questions and graph structure
            if (graphTaskForm.getQuestions().equals(new CreateGraphTaskForm(graphTask).getQuestions())) {
                return;
            }
            List<Question> questions = questionFormsToQuestions(graphTaskForm.getQuestions());
            questionRepository.saveAll(questions);
            double newMaxPoints = calculateMaxPoints(questions.get(0), 0);
            removeOldQuestions(graphTask, questions);
            addNewQuestions(graphTask, questions);
            graphTask.setMaxPoints(newMaxPoints);
        }

        @Transactional
        public void removeOldQuestions(GraphTask graphTask, List<Question> questions) {
            graphTask.getQuestions().removeIf(question -> !questions.contains(question));
        }

        @Transactional
        public void addNewQuestions(GraphTask graphTask, List<Question> questions) {
            for (Question q: questions) {
                if (!graphTask.getQuestions().contains(q)) {
                    graphTask.getQuestions().add(q);
                }
            }
        }


        public List<GraphNode> getGraphMap(Long graphTaskID) throws EntityNotFoundException {
            log.info("Fetching graph nodes for GraphTask with id {}", graphTaskID);
            GraphTask graphTask = graphTaskRepository.findGraphTaskById(graphTaskID);
            graphTaskValidator.validateGraphTaskIsNotNull(graphTask, graphTaskID);

            if (graphTask.getQuestions().isEmpty()) {
                return List.of();
            }

            LinkedList<GraphNode> graphMap = new LinkedList<>();
            for (Question question: graphTask.getQuestions()) {
                fillGraphMap(question, graphMap);
            }
            return graphMap;
        }

        private void fillGraphMap(Question question, List<GraphNode> graph) {
            GraphNode node = new GraphNode(
                    question.getId(),
                    question.getDifficulty() != null ? question.getDifficulty().getDifficulty() : null,
                    question.getNext().stream().map(Question::getId).toList()
            );
            if (graph.stream().noneMatch(n -> n.getQuestionID() == question.getId())) {
                graph.add(node);
            }

            for (Question q: question.getNext()) {
                fillGraphMap(q, graph);
            }
        }
    }
}
