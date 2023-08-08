package com.example.api.group.model;

import com.example.api.course.model.Course;
import com.example.api.course.model.CourseMember;
import com.example.api.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"group\"")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "group")
    @JsonBackReference
    private List<User> users;

    @OneToMany(mappedBy = "group")
    @JsonBackReference
    private List<CourseMember> members;

    private String invitationCode;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Group(Long id, String name, String invitationCode, Course course) {
        this.id = id;
        this.name = name;
        this.invitationCode = invitationCode;
        this.course = course;
        this.users = new ArrayList<>();
        this.members = new ArrayList<>();
    }
}
