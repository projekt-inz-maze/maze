package com.example.api.config;

import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.model.FileTask;
import com.example.api.activity.task.model.GraphTask;
import com.example.api.activity.task.model.Info;
import com.example.api.activity.task.model.Survey;
import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.course.repository.CourseMemberRepository;
import com.example.api.course.repository.CourseRepository;
import com.example.api.course.service.CourseMemberService;
import com.example.api.group.model.AccessDate;
import com.example.api.group.model.Group;
import com.example.api.map.model.ActivityMap;
import com.example.api.map.model.Chapter;
import com.example.api.map.model.requirement.*;
import com.example.api.question.model.Difficulty;
import com.example.api.question.model.Option;
import com.example.api.question.model.Question;
import com.example.api.question.model.QuestionType;
import com.example.api.user.hero.HeroRepository;
import com.example.api.user.hero.model.*;
import com.example.api.user.model.AccountType;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.user.model.badge.*;
import com.example.api.util.model.File;
import com.example.api.util.model.Image;
import com.example.api.util.model.ImageType;
import com.example.api.util.model.Url;
import com.example.api.map.repository.ChapterRepository;
import com.example.api.map.repository.RequirementRepository;
import com.example.api.user.repository.*;
import com.example.api.util.repository.FileRepository;
import com.example.api.util.repository.UrlRepository;
import com.example.api.activity.feedback.service.ProfessorFeedbackService;
import com.example.api.activity.feedback.service.SurveyResultService;
import com.example.api.activity.result.service.FileTaskResultService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.activity.task.service.FileTaskService;
import com.example.api.activity.task.service.GraphTaskService;
import com.example.api.activity.task.service.InfoService;
import com.example.api.activity.task.service.SurveyService;
import com.example.api.group.service.AccessDateService;
import com.example.api.group.service.GroupService;
import com.example.api.map.service.ActivityMapService;
import com.example.api.map.service.RequirementService;
import com.example.api.question.service.OptionService;
import com.example.api.question.service.QuestionService;
import com.example.api.user.service.BadgeService;
import com.example.api.user.service.UserService;
import com.example.api.util.message.MessageManager;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
@Transactional
public class DatabaseConfig {
    private final UrlRepository urlRepository;
    private final ChapterRepository chapterRepository;
    private final RankRepository rankRepository;
    private final AdditionalPointsRepository additionalPointsRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final RequirementRepository requirementRepository;
    private final HeroRepository heroRepository;
    private final CourseRepository courseRepository;
    private final CourseMemberRepository courseMemberRepository;

    @Bean
    public CommandLineRunner commandLineRunner(UserService userService, CourseMemberService courseMemberService, ProfessorFeedbackService professorFeedbackService,
                                               SurveyResultService surveyResultService, GraphTaskService graphTaskService,
                                               GraphTaskResultService graphTaskResultService, GroupService groupService,
                                               ActivityMapService activityMapService, QuestionService questionService,
                                               FileTaskResultService fileTaskResultService, OptionService optionService,
                                               AccessDateService accessDateService, RequirementService requirementService,
                                               FileTaskService fileTaskService, InfoService infoService,
                                               SurveyService surveyService, BadgeService badgeService){
        return args -> {

            Course course1 = new Course(null, "course1", "description for course1", false, null);
            Course course2 = new Course(null, "course2", "description for course1", false, null);
            courseRepository.save(course1);
            courseRepository.save(course2);

            // HEROES
            long week = TimeUnit.DAYS.toMillis(7);
            Hero priest = new Priest(HeroType.PRIEST, week, course1);
            Hero priest2 = new Priest(HeroType.PRIEST, week, course2);
            Hero rogue = new Rogue(HeroType.ROGUE, week, course1);
            Hero warrior = new Warrior(HeroType.WARRIOR, week, course1);
            Hero wizard = new Wizard(HeroType.WIZARD, week, course1);
            heroRepository.saveAll(List.of(priest, rogue, wizard, warrior, priest2));

            // USERS & GROUPS
            List<User> students1 = Collections.synchronizedList(new ArrayList<>());
            students1.add(createStudent("jgorski@student.agh.edu.pl", "Jerzy", "Górski", 123456));
            students1.add(createStudent("smazur@student.agh.edu.pl", "Szymon", "Mazur", 123457));
            students1.add(createStudent("murbanska@student.agh.edu.pl", "Matylda", "Urbańska",123458));
            students1.add(createStudent("pwasilewski@student.agh.edu.pl", "Patryk", "Wasilewski",123459));
            students1.add(createStudent("awojcik@student.agh.edu.pl", "Amelia", "Wójcik",223456));
            students1.add(createStudent("kkruk@student.agh.edu.pl", "Kornel", "Kruk",323456));
            students1.add(createStudent("mdabrowska@student.agh.edu.pl", "Maria", "Dąbrowska",423456));
            students1.add(createStudent("aczajkowski@student.agh.edu.pl", "Antoni", "Czajkowski",523456));

            userRepository.saveAll(students1);

            List<User> students2 = Collections.synchronizedList(new ArrayList<>());

            students2.add(createStudent("mnowak@student.agh.edu.pl", "Magdalena", "Nowak", 623456));
            students2.add(createStudent("jlewandowska@student.agh.edu.pl", "Julia", "Lewandowska", 723456));
            students2.add(createStudent("mwojcik@student.agh.edu.pl", "Milena", "Wójcik", 823456));
            students2.add(createStudent("kpaluch@student.agh.edu.pl", "Kacper", "Paluch", 923456));
            students2.add(createStudent("fzalewski@student.agh.edu.pl", "Filip", "Zalewski", 133456));
            students2.add(createStudent("jmichalak@student.agh.edu.pl", "Jan", "Michalak", 143456));
            students2.add(createStudent("kostrowska@student.agh.edu.pl", "Karina", "Ostrowska", 153456));
            students2.add(createStudent("dkowalska@student.agh.edu.pl", "Dominika", "Kowalska", 163456));
            students2.add(createStudent("manowak@student.agh.edu.pl", "Małgorzata Anna", "Kowalska", 163457));

            userRepository.saveAll(students2);

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


            User professor1 = new User("bmaj@agh.edu.pl",
                    "Bernard",
                    "Maj",
                    AccountType.PROFESSOR);
            professor1.setPassword(passwordEncoder.encode("12345"));

            User professor2 = new User("szielinski@agh.edu.pl",
                    "Sławomir",
                    "Zieliński",
                    AccountType.PROFESSOR);
            professor2.setPassword(passwordEncoder.encode("12345"));
            userRepository.save(professor2);

            userRepository.saveAll(List.of(professor1, professor2));


            Group group = new Group();
            group.setInvitationCode("1111");
            group.setName("pn-1440-A");
            group.setUsers(students1);
            group.setCourse(course1);
            groupService.saveGroup(group);

            Group group1 = new Group();
            group1.setInvitationCode("2222");
            group1.setName("pn-1440-B");
            group1.setUsers(students2);
            group1.setCourse(course1);
            groupService.saveGroup(group1);

            Group group1course2 = new Group();
            group1course2.setInvitationCode("3333");
            group1course2.setName("xd");
            group1course2.setCourse(course2);
            groupService.saveGroup(group1course2);

            addToGroup(students1.get(0), group1course2, priest2);

            addToGroup(students1.get(0), group, priest);
            addToGroup(students1.get(1), group, rogue);
            addToGroup(students1.get(2), group, wizard);
            addToGroup(students1.get(3), group, warrior);
            addToGroup(students1.get(4), group, priest);
            addToGroup(students1.get(5), group, rogue);
            addToGroup(students1.get(6), group, wizard);
            addToGroup(students1.get(7), group, warrior);

            addToGroup(students2.get(0), group1, priest);
            addToGroup(students2.get(1), group1, rogue);
            addToGroup(students2.get(2), group1, wizard);
            addToGroup(students2.get(3), group1, warrior);
            addToGroup(students2.get(4), group1, priest);
            addToGroup(students2.get(5), group1, rogue);
            addToGroup(students2.get(6), group1, wizard);
            addToGroup(students2.get(7), group1, warrior);

            professor1.getCourses().add(course1);
            course1.setOwner(professor1);

            professor2.getCourses().add(course2);
            course2.setOwner(professor2);

            userRepository.save(professor1);
            courseRepository.save(course1);

            userRepository.save(professor2);
            courseRepository.save(course2);

            List<Group> groups = new ArrayList<>();
            groups.add(group);
            groups.add(group1);
            course1.setGroups(groups);
            courseRepository.save(course1);

            course2.setGroups(List.of(group1course2));
            courseRepository.save(course2);

            // TASKS
            List<Question> questions = addQuestionSet(course1, questionService, optionService);
            AccessDate ac1 = new AccessDate(null, System.currentTimeMillis(), System.currentTimeMillis(), List.of(group1));
            AccessDate ac2 = new AccessDate(null, System.currentTimeMillis(), System.currentTimeMillis(), List.of(group));
            accessDateService.saveAccessDate(ac1);
            accessDateService.saveAccessDate(ac2);

            GraphTask graphTask = new GraphTask();
            graphTask.setIsBlocked(false);
            graphTask.setQuestions(questions);
            graphTask.setTitle("Dżungla kabli");
            graphTask.setDescription("Przebij się przez gąszcz pytań związanych z łączeniem urządzeń w lokalnej sieci i odkryj tajemnice łączenia bulbulatorów ze sobą!");
            graphTask.setRequiredKnowledge("skrętki, rodzaje ich ekranowania, łączenie urządzeń różnych warstw ze sobą");
            graphTask.setMaxPoints(60.0);
            graphTask.setExperience(20D);
            graphTask.setTimeToSolveMillis(12 * 60 * 1000L);
            graphTask.setRequirements(createDefaultRequirements());
            graphTask.setProfessor(professor1);
            graphTask.setPosX(5);
            graphTask.setPosY(4);
            graphTask.setCourse(course1);

            graphTaskService.saveGraphTask(graphTask);

            List<Question> questions2 = addQuestionSet(course1, questionService, optionService);

            List<Requirement> graphTaskTwoReq = requirementService.getDefaultRequirements(true);

            GraphTask graphTaskTwo = new GraphTask();
            graphTaskTwo.setIsBlocked(false);
            graphTaskTwo.setQuestions(questions2);
            graphTaskTwo.setTitle("Dżungla kabli II");
            graphTaskTwo.setDescription("Przebij się przez gąszcz pytań związanych z łączeniem urządzeń w lokalnej sieci i odkryj tajemnice łączenia bulbulatorów ze sobą!");
            graphTaskTwo.setRequiredKnowledge("skrętki, rodzaje ich ekranowania, łączenie urządzeń różnych warstw ze sobą");
            graphTaskTwo.setMaxPoints(60.0);
            graphTaskTwo.setExperience(25D);
            graphTaskTwo.setTimeToSolveMillis(12 * 60 * 1000L);
            graphTaskTwo.setProfessor(professor1);
            graphTaskTwo.setPosX(2);
            graphTaskTwo.setPosY(2);
            graphTaskTwo.setCourse(course1);
            graphTaskTwo.setRequirements(graphTaskTwoReq);
            graphTaskTwo.setCourse(course1);

            graphTaskService.saveGraphTask(graphTaskTwo);

            FileTask fileTask = new FileTask();
            fileTask.setIsBlocked(false);
            fileTask.setPosX(3);
            fileTask.setPosY(3);
            fileTask.setTitle("Niszczator kabli");
            fileTask.setDescription("Jak złamałbyś kabel światłowodowy? Czym?");
            fileTask.setProfessor(professor1);
            fileTask.setMaxPoints(30.0);
            fileTask.setExperience(10D);
            fileTask.setCourse(course1);
            fileTask.setRequirements(createDefaultRequirements());

            fileTaskService.saveFileTask(fileTask);

            Info info1 = new Info();
            info1.setIsBlocked(false);
            info1.setPosX(3);
            info1.setPosY(0);
            info1.setTitle("Skrętki");
            info1.setDescription("Przewody internetowe da się podzielić także pod względem ich ekranowania.");
            info1.setContent(MessageManager.LOREM_IPSUM);
            info1.setRequirements(createDefaultRequirements());
            info1.setCourse(course1);

            Url url1 = new Url();
            Url url2 = new Url();
            url1.setUrl("https://upload.wikimedia.org/wikipedia/commons/c/cb/UTP_cable.jpg");
            url2.setUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/25_pair_color_code_chart.svg/800px-25_pair_color_code_chart.svg.png");
            urlRepository.save(url1);
            urlRepository.save(url2);
            info1.setImageUrls(List.of(url1, url2));
            info1.setTitle("Skrętki");
            info1.setExperience(10.0);
            info1.setProfessor(professor1);
            info1.setCourse(course1);
            infoService.saveInfo(info1);


            Survey survey = new Survey();
            survey.setIsBlocked(false);
            survey.setTitle("Example map feedback");
            survey.setDescription("Pomóż nam polepszyć kurs dzieląc się swoją opinią!");
            survey.setPosX(7);
            survey.setPosY(3);
            survey.setPoints(10.0);
            survey.setExperience(5D);
            survey.setRequirements(createDefaultRequirements());
            survey.setCourse(course1);
            surveyService.saveSurvey(survey);

            byte[] chapterImageBytes = getByteArrayForFile("src/main/resources/images/chapter_image.png");
            Image chapterImage = new Image("Chapter image 1", chapterImageBytes, ImageType.CHAPTER, course1);
            fileRepository.save(chapterImage);

            ActivityMap activityMap1 = new ActivityMap();
            activityMap1.setMapSizeX(8);
            activityMap1.setMapSizeY(5);
            activityMap1.setGraphTasks(List.of(graphTask, graphTaskTwo));
            activityMap1.setFileTasks(List.of(fileTask));
            activityMap1.setInfos(List.of(info1));
            activityMap1.setSurveys(List.of(survey));
            activityMap1.setImage(chapterImage);
            activityMapService.saveActivityMap(activityMap1);

            Calendar calendar = Calendar.getInstance();

            GraphTaskResult result1 = new GraphTaskResult();
            CourseMember result1Member = (students1.get(0).getCourseMember(course1).orElseThrow());

            result1.setGraphTask(graphTask);
            result1.setMember(result1Member);
            result1.setPointsReceived(12.0);
            addReceivedPointsForUser(result1Member, result1.getPointsReceived());
            result1.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 28);
            result1.setCourse(course1);
            result1.setStartDateMillis(calendar.getTimeInMillis());
            result1.setSendDateMillis(calendar.getTimeInMillis() + result1.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result1);

            GraphTaskResult result2 = new GraphTaskResult();
            result2.setGraphTask(graphTaskTwo);
            CourseMember result2Member = students1.get(1).getCourseMember(course1).orElseThrow();
            result2.setMember(result2Member);
            result2.setPointsReceived(10.0);
            addReceivedPointsForUser(result2Member, result2.getPointsReceived());
            result2.setCourse(course1);
            result2.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 13);
            result2.setStartDateMillis(calendar.getTimeInMillis());
            result2.setSendDateMillis(calendar.getTimeInMillis() + result2.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result2);

            GraphTaskResult result3 = new GraphTaskResult();
            result3.setGraphTask(graphTaskTwo);
            CourseMember result3Member = students2.get(0).getCourseMember(course1).orElseThrow();
            result3.setMember(result3Member);
            result3.setPointsReceived(11.0);
            addReceivedPointsForUser(result3Member, result3.getPointsReceived());
            result3.setTimeSpentSec(60 * 10);
            result3.setCourse(course1);
            calendar.set(2022, Calendar.APRIL, 14);
            result3.setStartDateMillis(calendar.getTimeInMillis());
            result3.setSendDateMillis(calendar.getTimeInMillis() + result2.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result3);

            GraphTaskResult result4 = new GraphTaskResult();
            result4.setGraphTask(graphTaskTwo);
            CourseMember result4Member = students2.get(1).getCourseMember(course1).orElseThrow();
            result4.setMember(result4Member);
            result4.setPointsReceived(30.5);
            addReceivedPointsForUser(result4Member, result4.getPointsReceived());
            result4.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 14);
            result4.setStartDateMillis(calendar.getTimeInMillis());
            result4.setCourse(course1);
            result4.setSendDateMillis(calendar.getTimeInMillis() + result2.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result4);

            FileTaskResult fileResult = new FileTaskResult();
            fileResult.setId(1L);
            fileResult.setFileTask(fileTask);
            CourseMember fileResultMember = students1.get(0).getCourseMember(course1).orElseThrow();
            fileResult.setMember(fileResultMember);
            fileResult.setEvaluated(false);
            fileResult.setAnswer("Lorem ipsum");
            calendar.set(2022, Calendar.JUNE, 11);
            fileResult.setSendDateMillis(calendar.getTimeInMillis());
            fileResult.setCourse(course1);
            fileTaskResultService.saveFileTaskResult(fileResult);

            Chapter chapter = new Chapter();
            chapter.setName("Rozdział 1");
            chapter.setPosX(2);
            chapter.setPosY(2);
            chapter.setActivityMap(activityMap1);
            chapter.setRequirements(requirementService.getDefaultRequirements(false));
            chapter.setIsBlocked(false);
            chapter.setCourse(course1);
            chapterRepository.save(chapter);

            calendar.set(2022, Calendar.JUNE, 15);
            AdditionalPoints additionalPoints = new AdditionalPoints();
            additionalPoints.setId(1L);
            CourseMember additionalPointsMember = students1.get(0).getCourseMember(course1).orElseThrow();
            additionalPoints.setMember(additionalPointsMember);
            additionalPoints.setPointsReceived(100D);
            additionalPoints.setSendDateMillis(calendar.getTimeInMillis());
            additionalPoints.setProfessorEmail(professor1.getEmail());
            additionalPoints.setDescription("Good job");
            addReceivedPointsForUser(additionalPointsMember, additionalPoints.getPointsReceived());
            additionalPoints.setCourse(course1);
            additionalPointsRepository.save(additionalPoints);

            SurveyResult surveyResult1 = new SurveyResult();
            surveyResult1.setSurvey(survey);
            surveyResult1.setId(1L);
            CourseMember surveyResult1Member = students1.get(0).getCourseMember(course1).orElseThrow();
            surveyResult1.setMember(surveyResult1Member);
            surveyResult1.setPointsReceived(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult1Member, surveyResult1.getPointsReceived());
            calendar.set(2022, Calendar.JUNE, 16);
            surveyResult1.setSendDateMillis(calendar.getTimeInMillis());
            surveyResult1.setCourse(course1);
            surveyResultRepository.save(surveyResult1);

            SurveyResult surveyResult2 = new SurveyResult();
            surveyResult2.setSurvey(survey);
            surveyResult2.setId(2L);
            CourseMember surveyResult2Member = students1.get(1).getCourseMember(course1).orElseThrow();
            surveyResult2.setMember(surveyResult2Member);
            surveyResult2.setPointsReceived(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult2Member, surveyResult2.getPointsReceived());
            calendar.set(2022, Calendar.JUNE, 18);
            surveyResult2.setSendDateMillis(calendar.getTimeInMillis());
            surveyResult2.setCourse(course1);
            surveyResultRepository.save(surveyResult2);

            SurveyResult surveyResult3 = new SurveyResult();
            surveyResult3.setSurvey(survey);
            surveyResult3.setId(3L);
            CourseMember surveyResult3Member = students2.get(2).getCourseMember(course1).orElseThrow();
            surveyResult3.setMember(surveyResult3Member);
            surveyResult3.setPointsReceived(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult3Member, surveyResult3.getPointsReceived());
            calendar.set(2022, Calendar.JUNE, 19);
            surveyResult3.setSendDateMillis(calendar.getTimeInMillis());
            surveyResult3.setCourse(course1);
            surveyResultRepository.save(surveyResult3);

            File file = new File();
            fileRepository.save(file);


            byte[] chapterImageBytes2 = getByteArrayForFile("src/main/resources/images/chapter_image2.png");
            Image chapterImage2 = new Image("Chapter image 2", chapterImageBytes2, ImageType.CHAPTER, course1);
            fileRepository.save(chapterImage2);

            byte[] chapterImageBytes3 = getByteArrayForFile("src/main/resources/images/chapter_image3.png");
            Image chapterImage3 = new Image("Chapter image 3", chapterImageBytes3, ImageType.CHAPTER, course1);
            fileRepository.save(chapterImage3);

            byte[] chapterImageBytes4 = getByteArrayForFile("src/main/resources/images/chapter_image4.png");
            Image chapterImage4 = new Image("Chapter image 4", chapterImageBytes4, ImageType.CHAPTER, course1);
            fileRepository.save(chapterImage4);

            byte[] chapterImageBytes5 = getByteArrayForFile("src/main/resources/images/chapter_image5.png");
            Image chapterImage5 = new Image("Chapter image 5", chapterImageBytes5, ImageType.CHAPTER, course1);
            fileRepository.save(chapterImage5);

            userRepository.saveAll(students1);
            userRepository.saveAll(students2);

            initAllRanks(course1);
            initAllRanks(course2);
            initBadges(course1);
        };
    }

    private void addToGroup(User user, Group group, Hero hero) {
        UserHero userHero = userHero(hero, group.getCourse());
        CourseMember cm = new CourseMember(user, group, userHero);
        courseMemberRepository.save(cm);
        user.getCourseMemberships().add(cm);
        group.getMembers().add(cm);
        group.getUsers().add(user);
        userRepository.save(user);
    }

    private List<Requirement> createDefaultRequirements() {
        DateFromRequirement dateFromRequirement = new DateFromRequirement(
                MessageManager.DATE_FROM_REQ_NAME,
                false,
                null
        );
        DateToRequirement dateToRequirement = new DateToRequirement(
                MessageManager.DATE_TO_REQ_NAME,
                false,
                null
        );
        FileTasksRequirement fileTasksRequirement = new FileTasksRequirement(
                MessageManager.FILE_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        GraphTasksRequirement graphTasksRequirement = new GraphTasksRequirement(
                MessageManager.GRAPH_TASKS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        GroupsRequirement groupsRequirement = new GroupsRequirement(
                MessageManager.GROUPS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        MinPointsRequirement minPointsRequirement = new MinPointsRequirement(
                MessageManager.MIN_POINTS_REQ_NAME,
                false,
                null
        );
        StudentsRequirements studentsRequirements = new StudentsRequirements(
                MessageManager.STUDENTS_REQ_NAME,
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

    private void initAllRanks(Course course) throws IOException {
        byte[] warriorImageBytes1 = getByteArrayForFile("src/main/resources/images/warrior1.png");
        Image warriorImage1 = new Image("Warrior rank image 1", warriorImageBytes1, ImageType.RANK, course);
        fileRepository.save(warriorImage1);

        byte[] warriorImageBytes2 = getByteArrayForFile("src/main/resources/images/warrior.png");
        Image warriorImage2 = new Image("Warrior rank image 2", warriorImageBytes2, ImageType.RANK, course);
        fileRepository.save(warriorImage2);

        byte[] warriorImageBytes3 = getByteArrayForFile("src/main/resources/images/swordsman.png");
        Image warriorImage3 = new Image("Warrior rank image 3", warriorImageBytes3, ImageType.RANK, course);
        fileRepository.save(warriorImage3);

        byte[] warriorImageBytes4 = getByteArrayForFile("src/main/resources/images/knight.png");
        Image warriorImage4 = new Image("Warrior rank image 4", warriorImageBytes4, ImageType.RANK, course);
        fileRepository.save(warriorImage4);

        byte[] warriorImageBytes5 = getByteArrayForFile("src/main/resources/images/knightHorse.png");
        Image warriorImage5 = new Image("Warrior rank image 5", warriorImageBytes5, ImageType.RANK, course);
        fileRepository.save(warriorImage5);

        byte[] wizardImageBytes1 = getByteArrayForFile("src/main/resources/images/wizard1.png");
        Image wizardImage1 = new Image("Wizard rank image 1", wizardImageBytes1, ImageType.RANK, course);
        fileRepository.save(wizardImage1);

        byte[] wizardImageBytes2 = getByteArrayForFile("src/main/resources/images/wizard2.png");
        Image wizardImage2 = new Image("Wizard rank image 2", wizardImageBytes2, ImageType.RANK, course);
        fileRepository.save(wizardImage2);

        byte[] wizardImageBytes3 = getByteArrayForFile("src/main/resources/images/wizard3.png");
        Image wizardImage3 = new Image("Wizard rank image 3", wizardImageBytes3, ImageType.RANK, course);
        fileRepository.save(wizardImage3);

        byte[] wizardImageBytes4 = getByteArrayForFile("src/main/resources/images/wizard4.png");
        Image wizardImage4 = new Image("Wizard rank image 4", wizardImageBytes4, ImageType.RANK, course);
        fileRepository.save(wizardImage4);

        byte[] wizardImageBytes5 = getByteArrayForFile("src/main/resources/images/wizard5.png");
        Image wizardImage5 = new Image("Wizard rank image 5", wizardImageBytes5, ImageType.RANK, course);
        fileRepository.save(wizardImage5);

        byte[] priestImageBytes1 = getByteArrayForFile("src/main/resources/images/priest1.png");
        Image priestImage1 = new Image("Priest rank image 1", priestImageBytes1, ImageType.RANK, course);
        fileRepository.save(priestImage1);

        byte[] priestImageBytes2 = getByteArrayForFile("src/main/resources/images/priest2.png");
        Image priestImage2 = new Image("Priest rank image 2", priestImageBytes2, ImageType.RANK, course);
        fileRepository.save(priestImage2);

        byte[] priestImageBytes3 = getByteArrayForFile("src/main/resources/images/priest3.png");
        Image priestImage3 = new Image("Priest rank image 3", priestImageBytes3, ImageType.RANK, course);
        fileRepository.save(priestImage3);

        byte[] priestImageBytes4 = getByteArrayForFile("src/main/resources/images/priest4.png");
        Image priestImage4 = new Image("Priest rank image 4", priestImageBytes4, ImageType.RANK, course);
        fileRepository.save(priestImage4);

        byte[] priestImageBytes5 = getByteArrayForFile("src/main/resources/images/priest5.png");
        Image priestImage5 = new Image("Priest rank image 5", priestImageBytes5, ImageType.RANK, course);
        fileRepository.save(priestImage5);

        byte[] rogueImageBytes1 = getByteArrayForFile("src/main/resources/images/rogue1.png");
        Image rogueImage1 = new Image("Rogue rank image 1", rogueImageBytes1, ImageType.RANK, course);
        fileRepository.save(rogueImage1);

        byte[] rogueImageBytes2 = getByteArrayForFile("src/main/resources/images/rogue2.png");
        Image rogueImage2 = new Image("Rogue rank image 2", rogueImageBytes2, ImageType.RANK, course);
        fileRepository.save(rogueImage2);

        byte[] rogueImageBytes3 = getByteArrayForFile("src/main/resources/images/rogue3.png");
        Image rogueImage3 = new Image("Rogue rank image 3", rogueImageBytes3, ImageType.RANK, course);
        fileRepository.save(rogueImage3);

        byte[] rogueImageBytes4 = getByteArrayForFile("src/main/resources/images/rogue4.png");
        Image rogueImage4 = new Image("Rogue rank image 4", rogueImageBytes4, ImageType.RANK, course);
        fileRepository.save(rogueImage4);

        byte[] rogueImageBytes5 = getByteArrayForFile("src/main/resources/images/rogue5.png");
        Image rogueImage5 = new Image("Rogue rank image 5", rogueImageBytes5, ImageType.RANK, course);
        fileRepository.save(rogueImage5);

        Rank warriorRank1 = new Rank(null, HeroType.WARRIOR, "Chłop", 0.0, warriorImage1, course);
        Rank warriorRank2 = new Rank(null, HeroType.WARRIOR, "Giermek", 100.0, warriorImage2, course);
        Rank warriorRank3 = new Rank(null, HeroType.WARRIOR, "Wojownik", 200.0, warriorImage3, course);
        Rank warriorRank4 = new Rank(null, HeroType.WARRIOR, "Rycerz", 300.0, warriorImage4, course);
        Rank warriorRank5 = new Rank(null, HeroType.WARRIOR, "Paladyn", 400.0, warriorImage5, course);

        Rank wizardRank1 = new Rank(null, HeroType.WIZARD, "Adept magii", 0.0, wizardImage1, course);
        Rank wizardRank2 = new Rank(null, HeroType.WIZARD, "Początkujący czarnoksiężnik", 100.0, wizardImage2, course);
        Rank wizardRank3 = new Rank(null, HeroType.WIZARD, "Czarnoksiężnik", 200.0, wizardImage3, course);
        Rank wizardRank4 = new Rank(null, HeroType.WIZARD, "Mistrz magii", 300.0,wizardImage4, course);
        Rank wizardRank5 = new Rank(null, HeroType.WIZARD, "Arcymistrz magii", 400.0, wizardImage5, course);

        Rank priestRank1 = new Rank(null, HeroType.PRIEST, "Duchowny", 0.0, priestImage1, course);
        Rank priestRank2 = new Rank(null, HeroType.PRIEST, "Mnich", 100.0, priestImage2, course);
        Rank priestRank3 = new Rank(null, HeroType.PRIEST, "Inkwizytor", 200.0, priestImage3, course);
        Rank priestRank4 = new Rank(null, HeroType.PRIEST, "Kapłan", 300.0, priestImage4, course);
        Rank priestRank5 = new Rank(null, HeroType.PRIEST, "Arcykapłan", 400.0, priestImage5, course);

        Rank rogueRank1 = new Rank(null, HeroType.ROGUE, "Złodziej", 0.0, rogueImage1, course);
        Rank rogueRank2 = new Rank(null, HeroType.ROGUE, "Zwiadowca", 100.0, rogueImage2, course);
        Rank rogueRank3 = new Rank(null, HeroType.ROGUE, "Zabójca", 200.0, rogueImage3, course);
        Rank rogueRank4 = new Rank(null, HeroType.ROGUE, "Skrytobójca", 300.0, rogueImage4, course);
        Rank rogueRank5 = new Rank(null, HeroType.ROGUE, "Przywódca bractwa", 400.0, rogueImage5, course);

        rankRepository.saveAll(List.of(warriorRank1, warriorRank2, warriorRank3, warriorRank4, warriorRank5));
        rankRepository.saveAll(List.of(wizardRank1, wizardRank2, wizardRank3, wizardRank4, wizardRank5));
        rankRepository.saveAll(List.of(priestRank1, priestRank2, priestRank3, priestRank4, priestRank5));
        rankRepository.saveAll(List.of(rogueRank1, rogueRank2, rogueRank3, rogueRank4, rogueRank5));
        courseRepository.save(course);
    }

    private byte[] getByteArrayForFile(String path) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new java.io.File(path));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", output);
        return output.toByteArray();
    }

    private void initBadges(Course course) throws IOException {
        Image activityMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/activity_master.png"), ImageType.BADGE, course);
        Image activityExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/activity_experienced.png"), ImageType.BADGE, course);
        Image fileTaskExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_experienced.png"), ImageType.BADGE, course);
        Image fileTaskFirstSteps = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_first_steps.png"), ImageType.BADGE, course);
        Image fileTaskMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_master.png"), ImageType.BADGE, course);
        Image topFive = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/five.png"), ImageType.BADGE, course);
        Image graphTaskExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_experienced.png"), ImageType.BADGE, course);
        Image graphTaskFirstSteps = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_first_steps.png"), ImageType.BADGE, course);
        Image graphTaskMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_master.png"), ImageType.BADGE, course);
        Image groupLeader = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/group_leader.png"), ImageType.BADGE, course);
        Image handshake = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/handshake.png"), ImageType.BADGE, course);
        Image inTheMiddle = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/in_the_middle.png"), ImageType.BADGE, course);
        Image itsTheBeginning = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/its_the_beginning.png"), ImageType.BADGE, course);
        Image leader = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/leader.png"), ImageType.BADGE, course);
        Image longA = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/long.png"), ImageType.BADGE, course);
        Image lookingUp = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/looking_up.png"), ImageType.BADGE, course);
        Image smileFromProfessor = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/smile.png"), ImageType.BADGE, course);
        Image theEnd = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/the_end.png"), ImageType.BADGE, course);
        Image topTwenty = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/twenty.png"), ImageType.BADGE, course);

        fileRepository.saveAll(List.of(activityMaster, activityExperienced, fileTaskExperienced,fileTaskFirstSteps,
                fileTaskMaster,topFive,graphTaskExperienced,graphTaskFirstSteps,graphTaskMaster,groupLeader
                ,handshake,inTheMiddle,itsTheBeginning,leader,longA,lookingUp, smileFromProfessor, theEnd, topTwenty));

        Badge badge1 = new ConsistencyBadge(
                null,
                "To dopiero początek",
                "Wykonaj co najmniej jedną aktywność w przeciągu tygodnia od poprzedniej aktywności (7 dni) przez okres miesiąca",
                itsTheBeginning,
                4,
                course
        );

        Badge badge2 = new ConsistencyBadge(
                null,
                "Długo jeszcze?",
                "Wykonaj co najmniej jedną aktywność w przeciągu tygodnia od poprzedniej aktywności (7 dni) przez okres 3 miesięcy",
                longA,
                12,
                course
        );

        Badge badge3 = new ConsistencyBadge(
                null,
                "To już jest koniec, ale czy na pewno?",
                "Wykonaj co najmniej jedną aktywność w przeciągu tygodnia od poprzedniej aktywności (7 dni) przez okres 6 mięsięcy",
                theEnd,
                24,
                course
        );

        Badge badge4 = new TopScoreBadge(
                null,
                "Topowowa dwudziestka",
                "Bądź w 20% najepszych użytkowników (liczone po wykonaniu 5 ekspedycji lub zadań bojowych)",
                topTwenty,
                0.2,
                false,
                course
        );


        Badge badge5 = new TopScoreBadge(
                null,
                "Topowa piątka",
                "Bądź w 5% najepszych użytkowników (liczone po wykonaniu 5 ekspedycji lub zadań bojowych)",
                topFive,
                0.05,
                false,
                course
        );

        Badge badge6 = new TopScoreBadge(
                null,
                "Lider grupy",
                "Bądź najepszym użytkownikiem w swojej grupie (liczone po wykonaniu 5 ekspedycji lub zadań bojowych)",
                groupLeader,
                0.0,
                true,
                course
        );

        Badge badge7 = new TopScoreBadge(
                null,
                "Lider",
                "Bądź najepszym użytkownikiem (liczone po wykonaniu 5 ekspedycji lub zadań bojowych)",
                leader,
                0.0,
                false,
                course
        );


        Badge badge8 = new GraphTaskNumberBadge(
                null,
                "Pierwsze kroki w ekspedycji",
                "Wykonaj swoją pierwszą ekspedycję",
                graphTaskFirstSteps,
                1,
                course
        );

        Badge badge9 = new GraphTaskNumberBadge(
                null,
                "Doświadczony w ekspedycjach",
                "Wykonaj 10 ekspedycji",
                graphTaskExperienced,
                10,
                course
        );

        Badge badge10 = new GraphTaskNumberBadge(
                null,
                "Zaprawiony w ekspedycjach",
                "Wykonaj 50 ekspedycji",
                graphTaskMaster,
                50,
                course
        );

        Badge badge11 = new FileTaskNumberBadge(
                null,
                "Pierwsze kroki w zadaniu bojowym",
                "Wykonaj swoje pierwsze zadanie bojowe",
                fileTaskFirstSteps,
                1,
                null
        );

        Badge badge12 = new FileTaskNumberBadge(
                null,
                "Doświadczony w zadaniach bojowych",
                "Wykonaj 10 zadań bojowych",
                fileTaskExperienced,
                10,
                course
        );

        Badge badge13 = new FileTaskNumberBadge(
                null,
                "Zaprawiony w zadaniach bojowych",
                "Wykonaj 50 zadań bojowych",
                fileTaskMaster,
                50,
                course
        );

        Badge badge14 = new ActivityNumberBadge(
                null,
                "Doświadczony w aktywnościach",
                "Wykonaj 30 aktywności",
                activityExperienced,
                30,
                course
        );

        Badge badge15 = new ActivityNumberBadge(
                null,
                "Zaprawiony w aktywnościach",
                "Wykonaj 100 aktywności",
                activityMaster,
                100,
                course
        );

        Badge badge16 = new ActivityScoreBadge(
                null,
                "Marsz ku lepszemu",
                "Posiadaj ponad 60% ze wszystkich punktów z wykonanych ekspedycji oraz zadań bojowych (liczone po wykonaniu 3 ekspedycji lub zadań bojowych)",
                lookingUp,
                0.6,
                false,
                course
        );

        Badge badge17 = new ActivityScoreBadge(
                null,
                "Uśmiech prowadzącego",
                "Posiadaj ponad 80% ze wszystkich punktów z wykonanych ekspedycji oraz zadań bojowych (liczone po wykonaniu 3 ekspedycji lub zadań bojowych)",
                smileFromProfessor,
                0.8,
                false,
                course
        );

        Badge badge18 = new ActivityScoreBadge(
                null,
                "Uścisk dłoni prowadzącego",
                "Posiadaj ponad 95% ze wszystkich punktów z wykonanych ekspedycji oraz zadań bojowych (liczone po wykonaniu 3 ekspedycji lub zadań bojowych)",
                handshake,
                0.95,
                false,
                course
        );

        Badge badge19 = new ActivityScoreBadge(
                null,
                "W sam środek tarczy",
                "Posiadaj 100% z ekspedycji lub zadania bojowego",
                inTheMiddle,
                1.0,
                true,
                course
        );

        badgeRepository.saveAll(List.of(badge1, badge2, badge3, badge4, badge5, badge6, badge7, badge8, badge9, badge10,
                badge11, badge12, badge13, badge14, badge15, badge16, badge17, badge18, badge19));
    }

    private void addReceivedPointsForUser(CourseMember student, Double points){
        student.setPoints(student.getPoints() + points);
    }

    private User createStudent(String email,
                               String name,
                               String lastName,
                               Integer indexNumber) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User student = new User(email,
                name,
                lastName,
                AccountType.STUDENT);
        student.setPassword(passwordEncoder.encode("12345"));
        student.setIndexNumber(indexNumber);
        return student;
    }

    private UserHero userHero(Hero hero, Course course) {
        return new UserHero(hero, 0, 0L, course);
    }

    private List<Question> addQuestionSet(Course course, QuestionService questionService, OptionService optionService) {
        Option option = new Option("hub z routerem", true, null);
        Option option1 = new Option("komputer z komputerem", false, null);
        Option option2 = new Option("switch z routerem", true, null);
        Option option3 = new Option("hub ze switchem", false, null);

        Option option4 = new Option("Tak", true, null);
        Option option5 = new Option("Nie", false, null);

        List<Option> options = List.of(option, option1, option2, option3, option4, option5);

        Question startQuestion = new Question();
        Question question1 = new Question(QuestionType.MULTIPLE_CHOICE, "Które urządzenia można połączyć ze sobą skrętką “prostą”?", "Kable",
                Difficulty.EASY, List.of(option, option1, option2, option3), 10.0, new LinkedList<>(), null, course);
        Question question2 = new Question(QuestionType.SINGLE_CHOICE, "Czy ciąg znaków 1001100101101010010110 to poprawnie zakodowany za pomocą kodu Manchester ciąg 10100111001?",
                "Manchester", Difficulty.MEDIUM, List.of(option4, option5), 20.0, new LinkedList<>(), null, course);
        Question question3 = new Question(QuestionType.OPENED, "Jeśli zawiniesz kabel kawałkiem folii aluminiowej, jaki rodzaj skrętki Ci to przypomina?",
                "?", Difficulty.HARD, null, 30.0, new LinkedList<>(), "FTP", course);
        Question question4 = new Question(QuestionType.OPENED, "Jaki rodzaj powszechnie używanego kabla byłby możliwy do użytku po użyciu jak skakanka? Dlaczego ten?",
                "Kable 2", Difficulty.MEDIUM, null, 20.0, new LinkedList<>(), "skrętka", course);
        Question question5 = new Question(QuestionType.OPENED, "Zakoduj swoje imię i nazwisko za pomocą kodowania NRZI. ",
                "Kable 2", Difficulty.HARD, null, 30.0, new LinkedList<>(), "Jan Kowalski", course);

        List<Question> questions = List.of(startQuestion, question1, question2, question3, question4, question5);

        questionService.saveQuestions(questions);

        startQuestion.getNext().addAll(List.of(question1, question2, question3));

        question1.getNext().addAll(List.of(question2, question4));
        question3.getNext().addAll(List.of(question5));

        questionService.saveQuestions(questions);
        optionService.saveAll(options);

        option.setQuestion(question1);
        option1.setQuestion(question1);
        option2.setQuestion(question1);
        option3.setQuestion(question1);
        option4.setQuestion(question2);
        option5.setQuestion(question2);
        optionService.saveAll(options);

        return questions;
    }
}
