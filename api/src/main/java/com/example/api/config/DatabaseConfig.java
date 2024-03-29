package com.example.api.config;

import com.example.api.activity.result.model.AdditionalPoints;
import com.example.api.activity.result.model.FileTaskResult;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.SurveyResult;
import com.example.api.activity.result.repository.AdditionalPointsRepository;
import com.example.api.activity.result.repository.SurveyResultRepository;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.info.Info;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.graphtask.GraphTaskService;
import com.example.api.chapter.requirement.model.*;
import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.coursemember.CourseMemberRepository;
import com.example.api.course.CourseRepository;
import com.example.api.course.coursemember.CourseMemberService;
import com.example.api.group.accessdate.AccessDate;
import com.example.api.group.Group;
import com.example.api.map.ActivityMap;
import com.example.api.chapter.Chapter;
import com.example.api.question.Difficulty;
import com.example.api.question.option.Option;
import com.example.api.question.Question;
import com.example.api.question.QuestionType;
import com.example.api.user.badge.BadgeRepository;
import com.example.api.user.badge.types.*;
import com.example.api.user.hero.HeroRepository;
import com.example.api.user.hero.model.*;
import com.example.api.user.model.AccountType;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.Rank;
import com.example.api.user.model.User;
import com.example.api.file.File;
import com.example.api.file.image.Image;
import com.example.api.file.image.ImageType;
import com.example.api.util.model.Url;
import com.example.api.chapter.ChapterRepository;
import com.example.api.chapter.requirement.RequirementRepository;
import com.example.api.user.repository.*;
import com.example.api.file.FileRepository;
import com.example.api.util.repository.UrlRepository;
import com.example.api.activity.feedback.ProfessorFeedbackService;
import com.example.api.activity.survey.SurveyResultService;
import com.example.api.activity.result.service.FileTaskResultService;
import com.example.api.activity.result.service.GraphTaskResultService;
import com.example.api.activity.task.filetask.FileTaskService;
import com.example.api.activity.info.InfoService;
import com.example.api.activity.survey.SurveyService;
import com.example.api.group.accessdate.AccessDateService;
import com.example.api.group.GroupService;
import com.example.api.map.ActivityMapService;
import com.example.api.chapter.requirement.RequirementService;
import com.example.api.question.option.OptionService;
import com.example.api.question.QuestionService;
import com.example.api.user.badge.BadgeService;
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
    private final long week = TimeUnit.DAYS.toMillis(7);
    private final PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

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

//            Course testCourse = new Course(null,
//                    "Sieci komputerowe",
//                    "Kurs sieci komputerowych w semestrz zimowym 2023",
//                    false,
//                    null);
//            courseRepository.save(testCourse);
//
//            Hero testPriest = new Priest(HeroType.PRIEST, week, testCourse);
//            Hero testRogue = new Rogue(HeroType.ROGUE, week, testCourse);
//            Hero testWarrior = new Warrior(HeroType.WARRIOR, week, testCourse);
//            Hero testWizard = new Wizard(HeroType.WIZARD, week, testCourse);
//            List<Hero> testHeroes = List.of(testPriest, testRogue, testWarrior, testWizard);
//            heroRepository.saveAll(testHeroes);
//
//            List<User> studentsMon13 = createTestUsers("pon13");
//            List<User> studentsMon15 = createTestUsers("pon15");
//            //List<User> studentsFri15 = createTestUsers("pt15");
//            //List<User> studentsFri16 = createTestUsers("pt16");
//            //List<User> studentsFri18 = createTestUsers("pt18");
//
//            User szielinski = new User("szielinski@agh.edu.pl",
//                    "Sławomir",
//                    "Zieliński",
//                    AccountType.PROFESSOR);
//            szielinski.setPassword(passwordEncoder.encode("12345"));
//            userRepository.save(szielinski);
//
//            Group groupMon13 = createGroup("pn-1300", studentsMon13, testCourse, groupService);
//            Group groupMon15 = createGroup("pn-1500", studentsMon15, testCourse, groupService);
////            Group groupFri15 = createGroup("pt-1500", studentsFri15, testCourse, groupService);
////            Group groupFri16 = createGroup("pt-1640", studentsFri16, testCourse, groupService);
////            Group groupFri18 = createGroup("pt-1820", studentsFri18, testCourse, groupService);
//
//            Random rand = new Random();
//            User student;
//
//            for (int i = 0; i < studentsMon13.size(); i++) {
//                student = studentsMon13.get(i);
//                addToGroup(student, groupMon13, testHeroes.get(rand.nextInt(testHeroes.size())));
//            }
//
//            for (int i = 0; i < studentsMon15.size(); i++) {
//                student = studentsMon15.get(i);
//                addToGroup(student, groupMon15, testHeroes.get(rand.nextInt(testHeroes.size())));
//            }
//
////            for (int i = 0; i < studentsFri15.size(); i++) {
////                student = studentsFri15.get(i);
////                addToGroup(student, groupFri15, testHeroes.get(rand.nextInt(testHeroes.size())));
////            }
////
////            for (int i = 0; i < studentsFri16.size(); i++) {
////                student = studentsFri16.get(i);
////                addToGroup(student, groupFri16, testHeroes.get(rand.nextInt(testHeroes.size())));
////            }
////
////            for (int i = 0; i < studentsFri18.size(); i++) {
////                student = studentsFri18.get(i);
////                addToGroup(student, groupFri18, testHeroes.get(rand.nextInt(testHeroes.size())));
////            }
//
//            szielinski.getCourses().add(testCourse);
//            testCourse.setOwner(szielinski);
//
//            userRepository.save(szielinski);
//            courseRepository.save(testCourse);
//
//            List<Group> testGroups = new ArrayList<>();
//            testGroups.add(groupMon13);
//            testGroups.add(groupMon15);
////            testGroups.add(groupFri15);
////            testGroups.add(groupFri16);
////            testGroups.add(groupFri18);
//
//            testCourse.setGroups(testGroups);
//            courseRepository.save(testCourse);
//
//            initAllRanks(testCourse);
//            initBadges(testCourse);


            /////////////////////////////////////////////////
             //////////// PREVIOUS CONFIGURATION ////////////
            ///////////////////////////////////////////////


            Course course1 = new Course(null, "course1", "description for course1", null);
            Course course2 = new Course(null, "course2", "description for course1", null);
            Course course3 = new Course(null, "Sieci komputerowe", "Kurs przedmiotu sieci komputerowe", null);

            courseRepository.save(course1);
            courseRepository.save(course2);
            courseRepository.save(course3);

            // HEROES
            Hero priest = new Priest(HeroType.PRIEST, week, course1);
            Hero priest3 = new Priest(HeroType.PRIEST, week, course3);
            Hero priest2 = new Priest(HeroType.PRIEST, week, course2);
            Hero rogue = new Rogue(HeroType.ROGUE, week, course1);
            Hero rogue3 = new Rogue(HeroType.ROGUE, week, course3);
            Hero warrior = new Warrior(HeroType.WARRIOR, week, course1);
            Hero warrior3 = new Warrior(HeroType.WARRIOR, week, course3);
            Hero wizard = new Wizard(HeroType.WIZARD, week, course1);
            Hero wizard3 = new Wizard(HeroType.WIZARD, week, course3);
            heroRepository.saveAll(List.of(priest, rogue, wizard, warrior, priest2));
            heroRepository.saveAll(List.of(priest3, rogue3, wizard3, warrior3));

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

            List<User> students3 = Collections.synchronizedList(new ArrayList<>());
            List<User> students4 = Collections.synchronizedList(new ArrayList<>());
            students4.add(createStudent(  "pon131@student.agh.edu.pl", "student", "1", 11));
            students4.add(createStudent(  "pon132@student.agh.edu.pl", "student", "2", 12));
            students4.add(createStudent(  "pon133@student.agh.edu.pl", "student", "3", 13));
            students4.add(createStudent(  "pon134@student.agh.edu.pl", "student", "4", 14));
            students4.add(createStudent(  "pon135@student.agh.edu.pl", "student", "5", 15));
            students4.add(createStudent(  "pon136@student.agh.edu.pl", "student", "6", 16));
            students4.add(createStudent(  "pon137@student.agh.edu.pl", "student", "7", 17));
            students3.add(createStudent(  "pon138@student.agh.edu.pl", "student", "8", 18));
            students3.add(createStudent(  "pon139@student.agh.edu.pl", "student", "9", 19));
            students3.add(createStudent(  "pon1310@student.agh.edu.pl", "student", "10", 110));
            students3.add(createStudent(  "pon1311@student.agh.edu.pl", "student", "11", 111));
            students3.add(createStudent(  "pon1312@student.agh.edu.pl", "student", "12", 112));
            students3.add(createStudent(  "pon1313@student.agh.edu.pl", "student", "13", 113));
            students3.add(createStudent(  "pon1314@student.agh.edu.pl", "student", "14", 114));
            students3.add(createStudent(  "pon1315@student.agh.edu.pl", "student", "15", 115));
            userRepository.saveAll(students3);
            userRepository.saveAll(students4);

            List<User> students5 = Collections.synchronizedList(new ArrayList<>());
            List<User> students6 = Collections.synchronizedList(new ArrayList<>());
            students5.add(createStudent(  "pon151@student.agh.edu.pl", "student", "1", 21));
            students5.add(createStudent(  "pon152@student.agh.edu.pl", "student", "2", 22));
            students5.add(createStudent(  "pon153@student.agh.edu.pl", "student", "3", 23));
            students5.add(createStudent(  "pon154@student.agh.edu.pl", "student", "4", 24));
            students5.add(createStudent(  "pon155@student.agh.edu.pl", "student", "5", 25));
            students5.add(createStudent(  "pon156@student.agh.edu.pl", "student", "6", 26));
            students5.add(createStudent(  "pon157@student.agh.edu.pl", "student", "7", 27));
            students6.add(createStudent(  "pon158@student.agh.edu.pl", "student", "8", 28));
            students6.add(createStudent(  "pon159@student.agh.edu.pl", "student", "9", 29));
            students6.add(createStudent(  "pon1510@student.agh.edu.pl", "student", "10", 210));
            students6.add(createStudent(  "pon1511@student.agh.edu.pl", "student", "11", 211));
            students6.add(createStudent(  "pon1512@student.agh.edu.pl", "student", "12", 212));
            students6.add(createStudent(  "pon1513@student.agh.edu.pl", "student", "13", 213));
            students6.add(createStudent(  "pon1514@student.agh.edu.pl", "student", "14", 214));
            students6.add(createStudent(  "pon1515@student.agh.edu.pl", "student", "15", 215));
            userRepository.saveAll(students5);
            userRepository.saveAll(students6);

            List<User> studentsFri15A = Collections.synchronizedList(new ArrayList<>());
            List<User> studentsFri15B = Collections.synchronizedList(new ArrayList<>());
            studentsFri15A.add(createStudent(  "pt151@student.agh.edu.pl", "student", "1", 31));
            studentsFri15A.add(createStudent(  "pt152@student.agh.edu.pl", "student", "2", 32));
            studentsFri15A.add(createStudent(  "pt153@student.agh.edu.pl", "student", "3", 33));
            studentsFri15A.add(createStudent(  "pt154@student.agh.edu.pl", "student", "4", 34));
            studentsFri15A.add(createStudent(  "pt155@student.agh.edu.pl", "student", "5", 35));
            studentsFri15A.add(createStudent(  "pt156@student.agh.edu.pl", "student", "6", 36));
            studentsFri15A.add(createStudent(  "pt157@student.agh.edu.pl", "student", "7", 37));
            studentsFri15B.add(createStudent(  "pt158@student.agh.edu.pl", "student", "8", 38));
            studentsFri15B.add(createStudent(  "pt159@student.agh.edu.pl", "student", "9", 39));
            studentsFri15B.add(createStudent(  "pt1510@student.agh.edu.pl", "student", "10", 310));
            studentsFri15B.add(createStudent(  "pt1511@student.agh.edu.pl", "student", "11", 311));
            studentsFri15B.add(createStudent(  "pt1512@student.agh.edu.pl", "student", "12", 312));
            studentsFri15B.add(createStudent(  "pt1513@student.agh.edu.pl", "student", "13", 313));
            studentsFri15B.add(createStudent(  "pt1514@student.agh.edu.pl", "student", "14", 314));
            studentsFri15B.add(createStudent(  "pt1515@student.agh.edu.pl", "student", "15", 315));
            userRepository.saveAll(studentsFri15A);
            userRepository.saveAll(studentsFri15B);

            List<User> studentsFri16A = Collections.synchronizedList(new ArrayList<>());
            List<User> studentsFri16B = Collections.synchronizedList(new ArrayList<>());
            studentsFri16A.add(createStudent(  "pt161@student.agh.edu.pl", "student", "1", 41));
            studentsFri16A.add(createStudent(  "pt162@student.agh.edu.pl", "student", "2", 42));
            studentsFri16A.add(createStudent(  "pt163@student.agh.edu.pl", "student", "3", 43));
            studentsFri16A.add(createStudent(  "pt164@student.agh.edu.pl", "student", "4", 44));
            studentsFri16A.add(createStudent(  "pt165@student.agh.edu.pl", "student", "5", 45));
            studentsFri16A.add(createStudent(  "pt166@student.agh.edu.pl", "student", "6", 46));
            studentsFri16A.add(createStudent(  "pt167@student.agh.edu.pl", "student", "7", 47));
            studentsFri16B.add(createStudent(  "pt168@student.agh.edu.pl", "student", "8", 48));
            studentsFri16B.add(createStudent(  "pt169@student.agh.edu.pl", "student", "9", 49));
            studentsFri16B.add(createStudent(  "pt1610@student.agh.edu.pl", "student", "10", 410));
            studentsFri16B.add(createStudent(  "pt1611@student.agh.edu.pl", "student", "11", 411));
            studentsFri16B.add(createStudent(  "pt1612@student.agh.edu.pl", "student", "12", 412));
            studentsFri16B.add(createStudent(  "pt1613@student.agh.edu.pl", "student", "13", 413));
            studentsFri16B.add(createStudent(  "pt1614@student.agh.edu.pl", "student", "14", 414));
            studentsFri16B.add(createStudent(  "pt1615@student.agh.edu.pl", "student", "15", 415));
            userRepository.saveAll(studentsFri16A);
            userRepository.saveAll(studentsFri16B);


            List<User> studentsFri18A = Collections.synchronizedList(new ArrayList<>());
            List<User> studentsFri18B = Collections.synchronizedList(new ArrayList<>());
            studentsFri18A.add(createStudent(  "pt181@student.agh.edu.pl", "student", "1", 51));
            studentsFri18A.add(createStudent(  "pt182@student.agh.edu.pl", "student", "2", 52));
            studentsFri18A.add(createStudent(  "pt183@student.agh.edu.pl", "student", "3", 53));
            studentsFri18A.add(createStudent(  "pt184@student.agh.edu.pl", "student", "4", 54));
            studentsFri18A.add(createStudent(  "pt185@student.agh.edu.pl", "student", "5", 55));
            studentsFri18A.add(createStudent(  "pt186@student.agh.edu.pl", "student", "6", 56));
            studentsFri18A.add(createStudent(  "pt187@student.agh.edu.pl", "student", "7", 57));
            studentsFri18B.add(createStudent(  "pt188@student.agh.edu.pl", "student", "8", 58));
            studentsFri18B.add(createStudent(  "pt189@student.agh.edu.pl", "student", "9", 59));
            studentsFri18B.add(createStudent(  "pt1810@student.agh.edu.pl", "student", "10", 510));
            studentsFri18B.add(createStudent(  "pt1811@student.agh.edu.pl", "student", "11", 511));
            studentsFri18B.add(createStudent(  "pt1812@student.agh.edu.pl", "student", "12", 512));
            studentsFri18B.add(createStudent(  "pt1813@student.agh.edu.pl", "student", "13", 513));
            studentsFri18B.add(createStudent(  "pt1814@student.agh.edu.pl", "student", "14", 514));
            studentsFri18B.add(createStudent(  "pt1815@student.agh.edu.pl", "student", "15", 515));
            userRepository.saveAll(studentsFri18A);
            userRepository.saveAll(studentsFri18B);


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


            Group group = createGroup("pn-1440-A", students1, course1, groupService);

            Group group1 = createGroup("pn-1440-B", students2, course1, groupService);

            Group group3 = createGroup("pn-1300a", students3, course3, groupService);
            Group group4 = createGroup("pn-1300b", students4, course3, groupService);

            Group group5 = createGroup("pn-1500a", students5, course3, groupService);
            Group group6 = createGroup("pn-1500b", students6, course3, groupService);


            Group groupFri15A = createGroup("pt-1500a", studentsFri15A, course3, groupService);
            Group groupFri15B = createGroup("pt-1500b", studentsFri15B, course3, groupService);


            Group groupFri16A = createGroup("pt-1640a", studentsFri16A, course3, groupService);
            Group groupFri16B = createGroup("pt-1640b", studentsFri16B, course3, groupService);


            Group groupFri18A = createGroup("pt-1820a", studentsFri18A, course3, groupService);
            Group groupFri18B = createGroup("pt-1820b", studentsFri18B, course3, groupService);

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

            addToGroup(students3.get(0), group3, priest);
            addToGroup(students3.get(1), group3, rogue);
            addToGroup(students3.get(2), group3, wizard);
            addToGroup(students3.get(3), group3, warrior);
            addToGroup(students3.get(4), group3, priest);
            addToGroup(students3.get(5), group3, rogue);
            addToGroup(students3.get(6), group3, wizard);
            addToGroup(students3.get(7), group3, warrior);

            addToGroup(students4.get(0), group4, priest);
            addToGroup(students4.get(1), group4, rogue);
            addToGroup(students4.get(2), group4, wizard);
            addToGroup(students4.get(3), group4, warrior);
            addToGroup(students4.get(4), group4, priest);
            addToGroup(students4.get(5), group4, rogue);
            addToGroup(students4.get(6), group4, wizard);

            addToGroup(students5.get(0), group5, priest);
            addToGroup(students5.get(1), group5, rogue);
            addToGroup(students5.get(2), group5, wizard);
            addToGroup(students5.get(3), group5, warrior);
            addToGroup(students5.get(4), group5, priest);
            addToGroup(students5.get(5), group5, rogue);
            addToGroup(students5.get(6), group5, wizard);
            addToGroup(students5.get(7), group5, warrior);

            addToGroup(students6.get(0), group6, priest);
            addToGroup(students6.get(1), group6, rogue);
            addToGroup(students6.get(2), group6, wizard);
            addToGroup(students6.get(3), group6, warrior);
            addToGroup(students6.get(4), group6, priest);
            addToGroup(students6.get(5), group6, rogue);
            addToGroup(students6.get(6), group6, wizard);


            addToGroup(studentsFri15A.get(0), groupFri15A, priest);
            addToGroup(studentsFri15A.get(1), groupFri15A, rogue);
            addToGroup(studentsFri15A.get(2), groupFri15A, wizard);
            addToGroup(studentsFri15A.get(3), groupFri15A, warrior);
            addToGroup(studentsFri15A.get(4), groupFri15A, priest);
            addToGroup(studentsFri15A.get(5), groupFri15A, rogue);
            addToGroup(studentsFri15A.get(6), groupFri15A, wizard);
            addToGroup(studentsFri15A.get(7), groupFri15A, warrior);
            addToGroup(studentsFri15B.get(0), groupFri15B, priest);
            addToGroup(studentsFri15B.get(1), groupFri15B, rogue);
            addToGroup(studentsFri15B.get(2), groupFri15B, wizard);
            addToGroup(studentsFri15B.get(3), groupFri15B, warrior);
            addToGroup(studentsFri15B.get(4), groupFri15B, priest);
            addToGroup(studentsFri15B.get(5), groupFri15B, rogue);
            addToGroup(studentsFri15B.get(6), groupFri15B, wizard);

            addToGroup(studentsFri16A.get(0), groupFri16A, priest);
            addToGroup(studentsFri16A.get(1), groupFri16A, rogue);
            addToGroup(studentsFri16A.get(2), groupFri16A, wizard);
            addToGroup(studentsFri16A.get(3), groupFri16A, warrior);
            addToGroup(studentsFri16A.get(4), groupFri16A, priest);
            addToGroup(studentsFri16A.get(5), groupFri16A, rogue);
            addToGroup(studentsFri16A.get(6), groupFri16A, wizard);
            addToGroup(studentsFri16A.get(7), groupFri16A, warrior);
            addToGroup(studentsFri16B.get(0), groupFri16B, priest);
            addToGroup(studentsFri16B.get(1), groupFri16B, rogue);
            addToGroup(studentsFri16B.get(2), groupFri16B, wizard);
            addToGroup(studentsFri16B.get(3), groupFri16B, warrior);
            addToGroup(studentsFri16B.get(4), groupFri16B, priest);
            addToGroup(studentsFri16B.get(5), groupFri16B, rogue);
            addToGroup(studentsFri16B.get(6), groupFri16B, wizard);

            addToGroup(studentsFri18A.get(0), groupFri18A, priest);
            addToGroup(studentsFri18A.get(1), groupFri18A, rogue);
            addToGroup(studentsFri18A.get(2), groupFri18A, wizard);
            addToGroup(studentsFri18A.get(3), groupFri18A, warrior);
            addToGroup(studentsFri18A.get(4), groupFri18A, priest);
            addToGroup(studentsFri18A.get(5), groupFri18A, rogue);
            addToGroup(studentsFri18A.get(6), groupFri18A, wizard);
            addToGroup(studentsFri18A.get(7), groupFri18A, warrior);
            addToGroup(studentsFri18B.get(0), groupFri18B, priest);
            addToGroup(studentsFri18B.get(1), groupFri18B, rogue);
            addToGroup(studentsFri18B.get(2), groupFri18B, wizard);
            addToGroup(studentsFri18B.get(3), groupFri18B, warrior);
            addToGroup(studentsFri18B.get(4), groupFri18B, priest);
            addToGroup(studentsFri18B.get(5), groupFri18B, rogue);
            addToGroup(studentsFri18B.get(6), groupFri18B, wizard);


            professor1.getCourses().add(course1);
            course1.setOwner(professor1);

            professor2.getCourses().add(course3);
            course3.setOwner(professor2);

            userRepository.save(professor1);
            courseRepository.save(course1);

            userRepository.save(professor2);
            courseRepository.save(course3);

            List<Group> groups = new ArrayList<>();
            groups.add(group);
            groups.add(group1);
            course1.setGroups(groups);
            courseRepository.save(course1);

            List<Group> groups3 = new ArrayList<>();
            groups.add(group3);
            groups.add(group4);
            groups.add(group5);
            groups.add(group6);
            groups.add(groupFri15A);
            groups.add(groupFri15B);
            groups.add(groupFri16A);
            groups.add(groupFri16A);
            groups.add(groupFri18A);
            groups.add(groupFri18B);
            course3.setGroups(groups3);
            courseRepository.save(course3);

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
            graphTask.setTaskContent("skrętki, rodzaje ich ekranowania, łączenie urządzeń różnych warstw ze sobą");
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
            graphTaskTwo.setTaskContent("skrętki, rodzaje ich ekranowania, łączenie urządzeń różnych warstw ze sobą");
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
            Image chapterImage = new Image("Chapter image 1", chapterImageBytes, ImageType.CHAPTER);
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
            result1.setPoints(12.0);
            addReceivedPointsForUser(result1Member, result1.getPoints());
            result1.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 28);
            result1.setStartDateMillis(calendar.getTimeInMillis());
            result1.setSendDateMillis(calendar.getTimeInMillis() + result1.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result1);

            GraphTaskResult result2 = new GraphTaskResult();
            result2.setGraphTask(graphTaskTwo);
            CourseMember result2Member = students1.get(1).getCourseMember(course1).orElseThrow();
            result2.setMember(result2Member);
            result2.setPoints(10.0);
            addReceivedPointsForUser(result2Member, result2.getPoints());
            result2.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 13);
            result2.setStartDateMillis(calendar.getTimeInMillis());
            result2.setSendDateMillis(calendar.getTimeInMillis() + result2.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result2);

            GraphTaskResult result3 = new GraphTaskResult();
            result3.setGraphTask(graphTaskTwo);
            CourseMember result3Member = students2.get(0).getCourseMember(course1).orElseThrow();
            result3.setMember(result3Member);
            result3.setPoints(11.0);
            addReceivedPointsForUser(result3Member, result3.getPoints());
            result3.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 14);
            result3.setStartDateMillis(calendar.getTimeInMillis());
            result3.setSendDateMillis(calendar.getTimeInMillis() + result2.getTimeSpentSec() / 1000);
            graphTaskResultService.saveGraphTaskResult(result3);

            GraphTaskResult result4 = new GraphTaskResult();
            result4.setGraphTask(graphTaskTwo);
            CourseMember result4Member = students2.get(1).getCourseMember(course1).orElseThrow();
            result4.setMember(result4Member);
            result4.setPoints(30.5);
            addReceivedPointsForUser(result4Member, result4.getPoints());
            result4.setTimeSpentSec(60 * 10);
            calendar.set(2022, Calendar.APRIL, 14);
            result4.setStartDateMillis(calendar.getTimeInMillis());
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
            additionalPoints.setPoints(100D);
            additionalPoints.setSendDateMillis(calendar.getTimeInMillis());
            additionalPoints.setProfessorEmail(professor1.getEmail());
            additionalPoints.setDescription("Good job");
            addReceivedPointsForUser(additionalPointsMember, additionalPoints.getPoints());
            additionalPointsRepository.save(additionalPoints);

            SurveyResult surveyResult1 = new SurveyResult();
            surveyResult1.setSurvey(survey);
            surveyResult1.setId(1L);
            CourseMember surveyResult1Member = students1.get(0).getCourseMember(course1).orElseThrow();
            surveyResult1.setMember(surveyResult1Member);
            surveyResult1.setPoints(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult1Member, surveyResult1.getPoints());
            calendar.set(2022, Calendar.JUNE, 16);
            surveyResult1.setSendDateMillis(calendar.getTimeInMillis());
            surveyResultRepository.save(surveyResult1);

            SurveyResult surveyResult2 = new SurveyResult();
            surveyResult2.setSurvey(survey);
            surveyResult2.setId(2L);
            CourseMember surveyResult2Member = students1.get(1).getCourseMember(course1).orElseThrow();
            surveyResult2.setMember(surveyResult2Member);
            surveyResult2.setPoints(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult2Member, surveyResult2.getPoints());
            calendar.set(2022, Calendar.JUNE, 18);
            surveyResult2.setSendDateMillis(calendar.getTimeInMillis());
            surveyResultRepository.save(surveyResult2);

            SurveyResult surveyResult3 = new SurveyResult();
            surveyResult3.setSurvey(survey);
            surveyResult3.setId(3L);
            CourseMember surveyResult3Member = students2.get(2).getCourseMember(course1).orElseThrow();
            surveyResult3.setMember(surveyResult3Member);
            surveyResult3.setPoints(survey.getMaxPoints());
            addReceivedPointsForUser(surveyResult3Member, surveyResult3.getPoints());
            calendar.set(2022, Calendar.JUNE, 19);
            surveyResult3.setSendDateMillis(calendar.getTimeInMillis());
            surveyResultRepository.save(surveyResult3);

            File file = new File();
            fileRepository.save(file);


            byte[] chapterImageBytes2 = getByteArrayForFile("src/main/resources/images/chapter_image2.png");
            Image chapterImage2 = new Image("Chapter image 2", chapterImageBytes2, ImageType.CHAPTER);
            fileRepository.save(chapterImage2);

            byte[] chapterImageBytes3 = getByteArrayForFile("src/main/resources/images/chapter_image3.png");
            Image chapterImage3 = new Image("Chapter image 3", chapterImageBytes3, ImageType.CHAPTER);
            fileRepository.save(chapterImage3);

            byte[] chapterImageBytes4 = getByteArrayForFile("src/main/resources/images/chapter_image4.png");
            Image chapterImage4 = new Image("Chapter image 4", chapterImageBytes4, ImageType.CHAPTER);
            fileRepository.save(chapterImage4);

            byte[] chapterImageBytes5 = getByteArrayForFile("src/main/resources/images/chapter_image5.png");
            Image chapterImage5 = new Image("Chapter image 5", chapterImageBytes5, ImageType.CHAPTER);
            fileRepository.save(chapterImage5);

            userRepository.saveAll(students1);
            userRepository.saveAll(students2);

            initAllRanks(course1);
            initAllRanks(course2);
            initAllRanks(course3);
            initBadges(course1);
            initBadges(course3);
        };
    }

    private static Group createGroup(String name, List<User> students, Course course, GroupService groupService) {
        Group group = new Group();
        group.setInvitationCode(name);
        group.setName(name);
        group.setUsers(students);
        group.setCourse(course);
        groupService.saveGroup(group);
        return group;
    }

    private void addToGroup(User user, Group group, Hero hero) {
        UserHero userHero = userHero(hero);
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
        StudentsRequirement studentsRequirement = new StudentsRequirement(
                MessageManager.STUDENTS_REQ_NAME,
                false,
                new LinkedList<>()
        );
        List<Requirement> requirements = List.of(
                dateFromRequirement,
                dateToRequirement,
                minPointsRequirement,
                groupsRequirement,
                studentsRequirement,
                graphTasksRequirement,
                fileTasksRequirement
        );

        requirementRepository.saveAll(requirements);
        return requirements;
    }

    private List<User> createTestUsers(String prefix) {
        List<User> students = new ArrayList<User>();
        for (int i = 0; i < 15; i++) {
            User student = createStudent(prefix + i, "Student", String.valueOf(i), i);
            students.add(student);
        }

        userRepository.saveAll(students);
        return students;
    }

    private void initAllRanks(Course course) throws IOException {
        byte[] warriorImageBytes1 = getByteArrayForFile("src/main/resources/images/warrior1.png");
        Image warriorImage1 = new Image("Warrior rank image 1", warriorImageBytes1, ImageType.RANK);
        fileRepository.save(warriorImage1);

        byte[] warriorImageBytes2 = getByteArrayForFile("src/main/resources/images/warrior.png");
        Image warriorImage2 = new Image("Warrior rank image 2", warriorImageBytes2, ImageType.RANK);
        fileRepository.save(warriorImage2);

        byte[] warriorImageBytes3 = getByteArrayForFile("src/main/resources/images/swordsman.png");
        Image warriorImage3 = new Image("Warrior rank image 3", warriorImageBytes3, ImageType.RANK);
        fileRepository.save(warriorImage3);

        byte[] warriorImageBytes4 = getByteArrayForFile("src/main/resources/images/knight.png");
        Image warriorImage4 = new Image("Warrior rank image 4", warriorImageBytes4, ImageType.RANK);
        fileRepository.save(warriorImage4);

        byte[] warriorImageBytes5 = getByteArrayForFile("src/main/resources/images/knightHorse.png");
        Image warriorImage5 = new Image("Warrior rank image 5", warriorImageBytes5, ImageType.RANK);
        fileRepository.save(warriorImage5);

        byte[] wizardImageBytes1 = getByteArrayForFile("src/main/resources/images/wizard1.png");
        Image wizardImage1 = new Image("Wizard rank image 1", wizardImageBytes1, ImageType.RANK);
        fileRepository.save(wizardImage1);

        byte[] wizardImageBytes2 = getByteArrayForFile("src/main/resources/images/wizard2.png");
        Image wizardImage2 = new Image("Wizard rank image 2", wizardImageBytes2, ImageType.RANK);
        fileRepository.save(wizardImage2);

        byte[] wizardImageBytes3 = getByteArrayForFile("src/main/resources/images/wizard3.png");
        Image wizardImage3 = new Image("Wizard rank image 3", wizardImageBytes3, ImageType.RANK);
        fileRepository.save(wizardImage3);

        byte[] wizardImageBytes4 = getByteArrayForFile("src/main/resources/images/wizard4.png");
        Image wizardImage4 = new Image("Wizard rank image 4", wizardImageBytes4, ImageType.RANK);
        fileRepository.save(wizardImage4);

        byte[] wizardImageBytes5 = getByteArrayForFile("src/main/resources/images/wizard5.png");
        Image wizardImage5 = new Image("Wizard rank image 5", wizardImageBytes5, ImageType.RANK);
        fileRepository.save(wizardImage5);

        byte[] priestImageBytes1 = getByteArrayForFile("src/main/resources/images/priest1.png");
        Image priestImage1 = new Image("Priest rank image 1", priestImageBytes1, ImageType.RANK);
        fileRepository.save(priestImage1);

        byte[] priestImageBytes2 = getByteArrayForFile("src/main/resources/images/priest2.png");
        Image priestImage2 = new Image("Priest rank image 2", priestImageBytes2, ImageType.RANK);
        fileRepository.save(priestImage2);

        byte[] priestImageBytes3 = getByteArrayForFile("src/main/resources/images/priest3.png");
        Image priestImage3 = new Image("Priest rank image 3", priestImageBytes3, ImageType.RANK);
        fileRepository.save(priestImage3);

        byte[] priestImageBytes4 = getByteArrayForFile("src/main/resources/images/priest4.png");
        Image priestImage4 = new Image("Priest rank image 4", priestImageBytes4, ImageType.RANK);
        fileRepository.save(priestImage4);

        byte[] priestImageBytes5 = getByteArrayForFile("src/main/resources/images/priest5.png");
        Image priestImage5 = new Image("Priest rank image 5", priestImageBytes5, ImageType.RANK);
        fileRepository.save(priestImage5);

        byte[] rogueImageBytes1 = getByteArrayForFile("src/main/resources/images/rogue1.png");
        Image rogueImage1 = new Image("Rogue rank image 1", rogueImageBytes1, ImageType.RANK);
        fileRepository.save(rogueImage1);

        byte[] rogueImageBytes2 = getByteArrayForFile("src/main/resources/images/rogue2.png");
        Image rogueImage2 = new Image("Rogue rank image 2", rogueImageBytes2, ImageType.RANK);
        fileRepository.save(rogueImage2);

        byte[] rogueImageBytes3 = getByteArrayForFile("src/main/resources/images/rogue3.png");
        Image rogueImage3 = new Image("Rogue rank image 3", rogueImageBytes3, ImageType.RANK);
        fileRepository.save(rogueImage3);

        byte[] rogueImageBytes4 = getByteArrayForFile("src/main/resources/images/rogue4.png");
        Image rogueImage4 = new Image("Rogue rank image 4", rogueImageBytes4, ImageType.RANK);
        fileRepository.save(rogueImage4);

        byte[] rogueImageBytes5 = getByteArrayForFile("src/main/resources/images/rogue5.png");
        Image rogueImage5 = new Image("Rogue rank image 5", rogueImageBytes5, ImageType.RANK);
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
        Image activityMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/activity_master.png"), ImageType.BADGE);
        Image activityExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/activity_experienced.png"), ImageType.BADGE);
        Image fileTaskExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_experienced.png"), ImageType.BADGE);
        Image fileTaskFirstSteps = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_first_steps.png"), ImageType.BADGE);
        Image fileTaskMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/file_task_master.png"), ImageType.BADGE);
        Image topFive = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/five.png"), ImageType.BADGE);
        Image graphTaskExperienced = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_experienced.png"), ImageType.BADGE);
        Image graphTaskFirstSteps = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_first_steps.png"), ImageType.BADGE);
        Image graphTaskMaster = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/graph_task_master.png"), ImageType.BADGE);
        Image groupLeader = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/group_leader.png"), ImageType.BADGE);
        Image handshake = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/handshake.png"), ImageType.BADGE);
        Image inTheMiddle = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/in_the_middle.png"), ImageType.BADGE);
        Image itsTheBeginning = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/its_the_beginning.png"), ImageType.BADGE);
        Image leader = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/leader.png"), ImageType.BADGE);
        Image longA = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/long.png"), ImageType.BADGE);
        Image lookingUp = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/looking_up.png"), ImageType.BADGE);
        Image smileFromProfessor = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/smile.png"), ImageType.BADGE);
        Image theEnd = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/the_end.png"), ImageType.BADGE);
        Image topTwenty = new Image("Badge", getByteArrayForFile("src/main/resources/images/badge/twenty.png"), ImageType.BADGE);

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
        User student = new User(email,
                name,
                lastName,
                AccountType.STUDENT);
        student.setPassword(passwordEncoder.encode("12345"));
        student.setIndexNumber(indexNumber);
        student.setPersonality(new HashMap<>());
        return student;
    }

    private UserHero userHero(Hero hero) {
        return new UserHero(hero, 0, 0L);
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
                Difficulty.EASY, List.of(option, option1, option2, option3), 10.0, new LinkedList<>(), null);
        Question question2 = new Question(QuestionType.SINGLE_CHOICE, "Czy ciąg znaków 1001100101101010010110 to poprawnie zakodowany za pomocą kodu Manchester ciąg 10100111001?",
                "Manchester", Difficulty.MEDIUM, List.of(option4, option5), 20.0, new LinkedList<>(), null);
        Question question3 = new Question(QuestionType.OPENED, "Jeśli zawiniesz kabel kawałkiem folii aluminiowej, jaki rodzaj skrętki Ci to przypomina?",
                "?", Difficulty.HARD, null, 30.0, new LinkedList<>(), "FTP");
        Question question4 = new Question(QuestionType.OPENED, "Jaki rodzaj powszechnie używanego kabla byłby możliwy do użytku po użyciu jak skakanka? Dlaczego ten?",
                "Kable 2", Difficulty.MEDIUM, null, 20.0, new LinkedList<>(), "skrętka");
        Question question5 = new Question(QuestionType.OPENED, "Zakoduj swoje imię i nazwisko za pomocą kodowania NRZI. ",
                "Kable 2", Difficulty.HARD, null, 30.0, new LinkedList<>(), "Jan Kowalski");

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
