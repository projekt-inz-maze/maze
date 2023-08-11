package com.example.api.error.exception;

public class ExceptionMessage {
    public static final String EMAIL_TAKEN = "Podany adres email jest zajęty";
    public static final String INDEX_TAKEN = "Podany numer indeksu jest zajęty";
    public static final String GROUP_CODE_NOT_EXIST = "Podany kod grupy nie istnieje";
    public static final String GROUP_NAME_TAKEN = "Podana nazwa grupy jest zajęta";
    public static final String COURSE_NAME_TAKEN = "Podana nazwa kursu jest zajęta";
    public static final String GROUP_CODE_TAKEN = "Podany kod grupy jest zajęty";
    public static final String FORM_FIELDS_NOT_NULL = "Wszystkie pola w tym pliku json muszą mieć wartość";
    public static final String GRAPH_TASK_FORM_FIELDS_NOT_NULL = "Wszystkie pola w tym pliku json w głównym obiekcie muszą mieć wartość";
    public static final String GRAPH_TASK_QUESTIONS_SIZE = "Lista z pytaniami musi posiadać conajmniej 2 elementy";
    public static final String GRAPH_TASK_QUESTIONS_FIRST_INDEX = "Pierwsze pytanie musi posiadać pole questionNum równe 0";
    public static final String GRAPH_TASK_QUESTIONS_NUM = "Wszystkie pytania musią posiadać pole questionNum";
    public static final String GRAPH_TASK_FIELDS_REQ = "Pola questionType, content, hint, difficulty, points, nextQuestions muszą mieć wartość";
    public static final String GRAPH_TASK_FIELDS_ANSWERS_OPENED = "Pole answers dla pytania typu OPENED musi posiadać wartośc null lub pustą tablicę";
    public static final String GRAPH_TASK_FIELDS_ANSWER_OPENED = "Pole answerForOpenedQuestion dla pytania typu OPENED nie może być nullem lub pustym stringiem";
    public static final String GRAPH_TASK_FIELDS_ANSWER_SINGLE = "Odpowiedzi dla pytania typu SINGLE_CHOICE muszą posiadać JEDNĄ prawidłową odpowiedź";
    public static final String GRAPH_TASK_FIELDS_QUESTIONS_FIRST = "Dla pierwszego pytania pole nextQuestions nie może być nullem";
    public static final String GRAPH_TASK_FIELDS_ANSWERS_SINGLE_MULTIPLE ="Pole answers dla pytania typu SINGLE_CHOICE i MULTIPLE_CHOICE nie może być nullem i musi posiadać conajmniej 2 elementy";
    public static final String GRAPH_TASK_FIELDS_ANSWER_SINGLE_MULTIPLE ="Pole answerForOpenedQuestion dla pytania typu SINGLE_CHOICE i MULTIPLE_CHOICE powinno być nullem lub pustym stringiem";
    public static final String OPTION_FORM_FIELDS ="Każde pole w odpowiedzi do pytania powinno miec wartość";
    public static final String INVALID_QUESTION_TYPE ="Pole questionType powinno mieć wartość OPENED / SINGLE_CHOICE / MULTIPLE_CHOICE";
    public static final String INVALID_DIFFICULTY ="Pole difficulty powinno mieć wartość EASY / MEDIUM / HARD";
    public static final String CONTENT_LEN_TOO_BIG ="Liczba znaków w polu content nie może przekraczać 1000";
    public static final String GRAPH_TASK_TITLE_NOT_UNIQUE ="Tytuł ekspedycji musi byc unikalny";
    public static final String FILE_TASK_TITLE_NOT_UNIQUE ="Tytuł zadania bojowego musi byc unikalny";
    public static final String TWO_ACTIVITIES_ON_THE_SAME_POSITION ="Dwie aktywności nie mogą być na takiej samej pozycji!";
    public static final String ACTIVITY_OUTSIDE_BOUNDARIES ="Pozycja aktywności musi mieścić się w obszarze mapy!";
    public static final String TWO_CHAPTERS_ON_THE_SAME_POSITION ="Dwa rozdziały nie mogą być na takiej samej pozycji!";
    public static final String GROUP_NOT_FOUND ="Grupa o podanej nazwie nie istnieje: ";
    public static final String STUDENT_NOT_FOUND ="Student o podanym emailu nie istnieje: ";
    public static final String GRAPH_TASK_NOT_FOUND ="Ekspedycja o podanej nazwie nie istnieje: ";
    public static final String FILE_TASK_NOT_FOUND ="Zadanie bojowe o podanej nazwie nie istnieje: ";
    public static final String GROUP_NAME_CONTAINS_SEMICOLON ="Nazwa grupy nie może zawierać średnika";
    public static final String EMAIL_CONTAINS_SEMICOLON ="Email nie może zawierać średnika";
    public static final String EMAIL_WRONG_DOMAIN ="Email nie pochodzi z domeny AGH";
    public static final String FILE_TASK_TITLE_CONTAINS_SEMICOLON ="Nazwa zadania bojowego nie może zawierać średnika";
    public static final String GRAPH_TASK_TITLE_CONTAINS_SEMICOLON ="Nazwa ekspedycji nie może zawierać średnika";
    public static final String COURSE_CONTAINS_SEMICOLON ="Nazwa kursy nie może zawierać średnika";
    public static final String COURSE_OWNER_INVALID ="użytkownik nie jest prowadzącym kursu prowadzącego";
    public static final String TIME_REMAINING_IS_UP ="Czas na przejście ekspedycji się skończył";
    public static final String CHAPTER_MAP_SIZE_TOO_SMALL = "Podane wymiary mapy są zbyt małe dla istniejących na niej aktywności";
    public static final String IMAGE_NOT_EXISTS = "Zdjęcia o podanym id nie ma w bazie danych.";
    public static final String PROFESSOR_REGISTER_TOKEN_NOT_PASSED = "Token do rejestracji prowadzącego nie został podanny";
    public static final String WRONG_PROFESSOR_REGISTER_TOKEN = "Token do rejestracji prowadzącego jest niepoprawny";
    public static final String RANK_NAME_TOO_LONG = "Nazwa nie może przekraczać 30 znaków";
    public static final String SAME_RANK_MIN_POINTS = "Inna ranga posiada już podaną wartość progu punktowego";
    public static final String DATE_NOT_LONG = "Data musi być liczbą całkowitą";
    public static final String MIN_POINTS_NOT_DOUBLE = "Punkty muszą być liczbą zmiennaprzecinkową";
    public static final String USER_FEEDBACK_RATE_OUT_OF_RANGE = "Ocena w ankiecie musi być w zakresie 1-5";
    public static final String STRING_VALUE_NOT_PARSABLE_TO_INT = "Wpisana wartość nie jest liczbą całkowitą";
    public static final String STRING_VALUE_NOT_PARSABLE_TO_DOUBLE = "Wpisana wartość nie jest liczbą zmiennoprzecinkową";
    public static final String BADGE_TITLE_TOO_LONG = "Tytuł nie może przekraczać 30 znaków";
    public static final String PASSWORD_RESET_TOKEN_IS_NULL = "Użytkownik nie posiada tokenu do resetu hasła";
    public static final String PASSWORD_RESET_TOKEN_IS_USED = "Token do resetu hasła został już użyty";
    public static final String PASSWORD_RESET_TOKEN_EXPIRED = "Ważność tokena do resetu hasła wygasła";
    public static final String PASSWORD_RESET_TOKEN_INCORRECT = "Podany token do resetu hasła jest nieprawidłowy";
    public static final String PASSWORD_NOT_MEET_REQUIREMENTS = "Hasło nie spełnia wymagań";
    public static final String ROLE_NOT_ALLOWED = "Rola nie jest dozwolona";

    public static String graphTaskResultAlreadyExists(Long taskId, Long userId) {
        return "GraphTaskResult for GraphTask  " + taskId + " and user " + userId + " already exists";
    }
}
