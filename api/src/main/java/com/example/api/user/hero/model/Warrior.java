package com.example.api.user.hero.model;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.course.Course;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.Question;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.User;
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
public class Warrior extends Hero{
    private Double multiplier = 0.5;

    public Warrior(HeroType type, Long coolDownTimeMillis, Course course) {
        super(type, coolDownTimeMillis, course);
    }

    @Override
    public SuperPowerResponse<?> useSuperPower(HeroVisitor visitor,
                                               User user,
                                               GraphTaskResult result,
                                               Question question) throws RequestValidationException {
        return visitor.visitWarrior(this, result, question);
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

    public String getCanBeUsedMessage(GraphTaskResult result) {
        return super.getCanBeUsedMessage(result);
    }
}
