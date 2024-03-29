package com.example.api.user.model;

import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.course.StudentNotEnrolledException;
import com.example.api.personality.PersonalityType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

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

    @OneToOne
    PasswordResetToken passwordResetToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<CourseMember> courseMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    private List<Course> courses = new LinkedList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyClass(PersonalityType.class)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<PersonalityType, Double> personality;

    public Optional<CourseMember> getCourseMember(Long courseId) {
        return courseMemberships.stream()
                .filter(member -> member.getCourse().getId().equals(courseId))
                .findAny();
    }

    public CourseMember getCourseMember(Long courseId, boolean bool) throws StudentNotEnrolledException {
        return courseMemberships.stream()
                .filter(member -> member.getCourse().getId().equals(courseId))
                .findAny()
                .orElseThrow(() -> new StudentNotEnrolledException(this, courseId));
    }

    public CourseMember getCourseMember(Course course, boolean bool) throws StudentNotEnrolledException {
        return courseMemberships.stream()
                .filter(member -> member.getCourse().equals(course))
                .findAny()
                .orElseThrow(() -> new StudentNotEnrolledException(this, course.getId()));
    }

    public Optional<CourseMember> getCourseMember(Course course) {
        return courseMemberships.stream()
                .filter(member -> member.getCourse().equals(course))
                .findAny();
    }

    public boolean inCourse(Long courseId) {
        return courseMemberships.stream().anyMatch(member -> member.getCourse().getId().equals(courseId));
    }
}
