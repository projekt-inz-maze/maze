package com.example.api.user.model;

import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
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
    private Double points = 0D;

    @OneToOne
    PasswordResetToken passwordResetToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<CourseMember> courseMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    private List<Course> courses = new LinkedList<>();

    public synchronized void changePoints(Double diff) {
        if (points + diff < 0) return;
        points = points + diff;
    }

    public Optional<CourseMember> getCourseMember(Long courseId) {
        return courseMemberships.stream()
                .filter(member -> member.getCourse().getId().equals(courseId))
                .findAny();
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
