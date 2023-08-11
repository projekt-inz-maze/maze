package com.example.api.course.model;

import com.example.api.group.model.Group;
import com.example.api.user.model.HeroType;
import com.example.api.user.model.User;
import com.example.api.user.model.badge.UnlockedBadge;
import com.example.api.user.model.hero.UserHero;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"courseMember\"")
public class CourseMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private Integer level;
    private Double points = 0D;

    @Embedded
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserHero userHero;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<UnlockedBadge> unlockedBadges = new LinkedList<>();

    public CourseMember(User user, Group group, UserHero userHero) {
        this.user = user;
        this.group = group;
        this.course = group.getCourse();
        this.userHero = userHero;
    }

    public synchronized void changePoints(Double diff) {
        if (points + diff < 0) return;
        points = points + diff;
    }

    public HeroType getHeroType() {
        return userHero.getHero().getType();
    }
}
