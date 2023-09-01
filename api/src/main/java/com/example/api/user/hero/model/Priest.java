package com.example.api.user.hero.model;

import com.example.api.activity.result.dto.response.SuperPowerResponse;
import com.example.api.activity.result.model.GraphTaskResult;
import com.example.api.course.model.Course;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.question.model.Question;
import com.example.api.user.hero.HeroType;
import com.example.api.user.model.User;
import com.example.api.user.hero.HeroVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Priest extends Hero{
    private int healValue = 20;

    public Priest(HeroType type, Long coolDownTimeMillis, Course course) {
        super(type, coolDownTimeMillis, course);
    }

    @Override
    public SuperPowerResponse<?> useSuperPower(HeroVisitor visitor,
                                               User user, GraphTaskResult result,
                                               Question question) throws RequestValidationException {
        return visitor.visitPriest(this, result);
    }

    @Override
    public void changeValue(Double value) {
        setHealValue(value.intValue());
    }

    @Override
    public Boolean isResultStatusCorrect(GraphTaskResult result) {
        return true;
    }

    public long getHealValueForLevel(int level) {
        return TimeUnit.SECONDS.toMillis(healValue) * level;
    }
}
