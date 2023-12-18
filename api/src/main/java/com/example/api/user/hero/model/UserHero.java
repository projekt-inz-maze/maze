package com.example.api.user.hero.model;

import com.example.api.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ManyToOne;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHero {
    @ManyToOne
    private Hero hero;
    private Integer timesSuperPowerUsedInResult;
    private Long lastSuperPowerUsageTimeMillis;
}
