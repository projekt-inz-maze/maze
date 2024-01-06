package com.example.api.group;

import com.example.api.course.Course;
import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
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

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    @JsonBackReference
    private List<User> users = Collections.synchronizedList(new ArrayList<>());

    @OneToMany(mappedBy = "group")
    private List<CourseMember> members = Collections.synchronizedList(new ArrayList<>());

    private String invitationCode;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    public Group(Long id, String name, String invitationCode, Course course) {
        this.id = id;
        this.name = name;
        this.invitationCode = invitationCode;
        this.course = course;
    }
}
