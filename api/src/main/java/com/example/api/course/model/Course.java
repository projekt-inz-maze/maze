package com.example.api.course.model;

import com.example.api.group.Group;
import com.example.api.chapter.Chapter;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"course\"")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "course")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Group> groups = new LinkedList<>();

    @OneToMany(mappedBy = "course")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CourseMember> courseMembers = new LinkedList<>();

    @OneToMany(mappedBy = "course")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Chapter> chapters = new LinkedList<>();

    public Course(Long id, String name, String description, Boolean isArchived, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isArchived = isArchived;
        this.owner = owner;
    }

    public List<User> getAllStudents() {
        return groups.stream()
                .flatMap(group -> group.getUsers().stream())
                .filter(user -> user.getAccountType().equals(AccountType.STUDENT))
                .toList();
    }
    public List<CourseMember> getAllMembers() {
        return groups.stream()
                .flatMap(group -> group.getMembers().stream())
                .toList();
    }
}
