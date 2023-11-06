package com.example.api.user.hero;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.course.model.CourseMember;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.question.Question;
import com.example.api.question.QuestionType;
import com.example.api.user.hero.model.*;
import com.example.api.util.calculator.TimeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeroVisitor {
    private final TimeCalculator timeCalculator;
    public SuperPowerResponse<Long> visitPriest(Priest priest,
                                                GraphTaskResult result) throws RequestValidationException {
        checkIfHeroPowerCanBeUsed(priest, result);
        CourseMember user = result.getMember();

        int level = user.getLevel();
        long healValue = priest.getHealValueForLevel(level);

        long startDateMillis = result.getStartDateMillis();
        long newStartDateMillis = startDateMillis + healValue;
        long timeToSolveMillis = result.getGraphTask().getTimeToSolveMillis();

        result.setStartDateMillis(newStartDateMillis);
        user.getUserHero().setLastSuperPowerUsageTimeMillis(System.currentTimeMillis());
        return new SuperPowerResponse<>(timeCalculator.getTimeRemaining(newStartDateMillis, timeToSolveMillis));
    }

    public SuperPowerResponse<Boolean> visitRogue(Rogue rogue, GraphTaskResult result) throws RequestValidationException {
        checkIfHeroPowerCanBeUsed(rogue, result);
        result.setStatus(ResultStatus.CHOOSE);

        result.getMember().getUserHero().setLastSuperPowerUsageTimeMillis(System.currentTimeMillis());
        return new SuperPowerResponse<>(true);
    }

    public SuperPowerResponse<QuestionType> visitWarrior(Warrior warrior,
                                                         GraphTaskResult result,
                                                         Question question) throws RequestValidationException {
        checkIfHeroPowerCanBeUsed(warrior, result);

        QuestionType type = question.getType();
        UserHero userHero = result.getMember().getUserHero();
        userHero.setTimesSuperPowerUsedInResult(userHero.getTimesSuperPowerUsedInResult() + 1);
        userHero.setLastSuperPowerUsageTimeMillis(System.currentTimeMillis());
        return new SuperPowerResponse<>(type);
    }

    public SuperPowerResponse<Double> visitWizard(Wizard wizard,
                                                  GraphTaskResult result,
                                                  Question question) throws RequestValidationException {
        checkIfHeroPowerCanBeUsed(wizard, result);

        Double points = question.getPoints();
        UserHero userHero = result.getMember().getUserHero();
        userHero.setTimesSuperPowerUsedInResult(userHero.getTimesSuperPowerUsedInResult() + 1);
        userHero.setLastSuperPowerUsageTimeMillis(System.currentTimeMillis());
        return new SuperPowerResponse<>(points);
    }

    private void checkIfHeroPowerCanBeUsed(Hero hero,
                                          GraphTaskResult result) throws RequestValidationException {
        if (!hero.canPowerBeUsed(result)) {
            throw new RequestValidationException("You cannot use hero power now!");
        }
    }
}
