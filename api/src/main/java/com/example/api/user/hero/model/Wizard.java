package com.example.api.user.hero.model;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.course.model.Course;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.model.Question;
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
public class Wizard extends Hero{
    private Double multiplier = 0.5;

    public Wizard(HeroType type, Long coolDownTimeMillis, Course course) {
        super(type, coolDownTimeMillis, course);
    }

    @Override
    public SuperPowerResponse<?> useSuperPower(HeroVisitor visitor,
                                               User user,
                                               GraphTaskResult result,
                                               Question question) throws RequestValidationException {
        return visitor.visitWizard(this, result, question);
    }

    @Override
    public void changeValue(Double value) {
        setMultiplier(value);
    }

    @Override
    public Boolean isResultStatusCorrect(GraphTaskResult result) {
        return result.getStatus() == ResultStatus.CHOOSE;
    }

    public Boolean canPowerBeUsed(GraphTaskResult result) {
        return super.canPowerBeUsed(result, multiplier);
    }

    public String getCanBeUsedMessage(User user, GraphTaskResult result) {
        if (result.isFinished()) {
            return HeroMessage.RESULT_FINISHED;
        }
        if (!isResultStatusCorrect(result)) {
            return HeroMessage.INCORRECT_STATUS;
        }

        int timesSuperPowerUsedInResult = result.getMember().getUserHero().getTimesSuperPowerUsedInResult();
        int level = result.getMember().getLevel();
        long timesSuperPowerCanBeUsed = Math.round(multiplier * level);
        if (timesSuperPowerUsedInResult != 0) {
            if (timesSuperPowerUsedInResult <= timesSuperPowerCanBeUsed) {
                return HeroMessage.CANNOT_USE_MORE;
            }
        } else {
            if (isCoolDownActive(result.getMember())) {
                return HeroMessage.COOL_DOWN_ACTIVE;
            }
        }
        String message = HeroMessage.POWER_READY_TO_BE_USED_WITH_NUMBER;
        return message.replace("{}", String.valueOf(timesSuperPowerCanBeUsed - timesSuperPowerUsedInResult));
    }
}
