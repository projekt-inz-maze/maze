package com.example.api.user.hero.model;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.course.model.Course;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.Question;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.User;
import com.example.api.util.message.HeroMessage;
import com.example.api.user.hero.HeroVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rogue extends Hero{
    private Double multiplier = 1.0;

    public Rogue(HeroType type, Long coolDownTimeMillis, Course course) {
        super(type, coolDownTimeMillis, course);
    }

    @Override
    public SuperPowerResponse<?> useSuperPower(HeroVisitor visitor,
                                               User user,
                                               GraphTaskResult result,
                                               Question question) throws RequestValidationException {
        return visitor.visitRogue(this, result);
    }

    @Override
    public Boolean isResultStatusCorrect(GraphTaskResult result) {
        return result.getStatus() == ResultStatus.ANSWER;
    }

    @Override
    public void changeValue(Double value) {
        setMultiplier(value);
    }

    public Boolean canPowerBeUsed(GraphTaskResult result) {
        int level = result.getMember().getLevel();
        double points = result.getCurrQuestion().getPoints();
        if (!canSkipQuestion(level, points)) {
            return false;
        }
        return super.canPowerBeUsed(result);
    }

    private boolean canSkipQuestion(int level, double points) {
        return level * multiplier >= points;
    }

    public String getCanBeUsedMessage(GraphTaskResult result) {
        if (result.isFinished()) {
            return HeroMessage.RESULT_FINISHED;
        }
        if (!isResultStatusCorrect(result)) {
            return HeroMessage.INCORRECT_STATUS;
        }
        if (isCoolDownActive(result.getMember())) {
            return HeroMessage.COOL_DOWN_ACTIVE;
        }
        int level = result.getMember().getLevel();
        double points = result.getCurrQuestion().getPoints();
        if (!canSkipQuestion(level, points)) {
            String message = HeroMessage.CANNOT_SKIP;
            double pointsToSkip = level * multiplier;
            return message.replace("{}", String.valueOf(pointsToSkip));
        }
        return HeroMessage.POWER_READY_TO_BE_USED;
    }
}
