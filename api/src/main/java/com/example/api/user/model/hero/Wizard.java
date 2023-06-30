package com.example.api.user.model.hero;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.activity.result.model.ResultStatus;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.model.Question;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.User;
import com.example.api.util.visitor.HeroVisitor;
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

    public Wizard(HeroType type, Long coolDownTimeMillis) {
        super(type, coolDownTimeMillis);
    }

    @Override
    public SuperPowerResponse<?> useSuperPower(HeroVisitor visitor,
                                               User user,
                                               GraphTaskResult result,
                                               Question question) throws RequestValidationException {
        return visitor.visitWizard(this, user, result, question);
    }

    @Override
    public void changeValue(Double value) {
        setMultiplier(value);
    }

    @Override
    public Boolean isResultStatusCorrect(GraphTaskResult result) {
        return result.getStatus() == ResultStatus.CHOOSE;
    }

    public Boolean canPowerBeUsed(User user, GraphTaskResult result) {
        return super.canPowerBeUsed(user, result, multiplier);
    }

    public String getCanBeUsedMessage(User user, GraphTaskResult result) {
        return super.getCanBeUsedMessage(user, result, multiplier);
    }
}
