package com.example.api.user.hero.model;

import com.example.api.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.ManyToOne;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHero {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Hero hero;
    private Integer timesSuperPowerUsedInResult;
    private Long lastSuperPowerUsageTimeMillis;
}
