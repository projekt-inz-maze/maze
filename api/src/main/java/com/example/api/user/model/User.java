package com.example.api.user.model;

import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.user.model.badge.UnlockedBadge;
import com.example.api.user.model.hero.UserHero;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"user\"")
public class User {

    public User(String email, String firstName, String lastName,
                AccountType accountType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountType = accountType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Integer indexNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private Integer level;
    private Double points = 0D;

    @Embedded
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserHero userHero;

    @OneToOne
    PasswordResetToken passwordResetToken;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "courseMembers",
            joinColumns = {@JoinColumn(name = "course_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "coursemember_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "course_id")
    private Map<Long, CourseMember> courseMemberships;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<UnlockedBadge> unlockedBadges = new LinkedList<>();

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    private List<Course> courses = new LinkedList<>();

    public synchronized void changePoints(Double diff) {
        if (points + diff < 0) return;
        points = points + diff;
    }

    public HeroType getHeroType() {
        if (accountType.equals(AccountType.STUDENT)) return userHero.getHero().getType();
        return null;
    }

    public Optional<CourseMember> getCourseMember(Long courseId) {
        return Optional.ofNullable(courseMemberships.get(courseId));
    }
    public boolean inCourse(Long courseId) {
        return courseMemberships.containsKey(courseId);
    }
}
