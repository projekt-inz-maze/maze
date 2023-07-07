package com.example.api.course.model;

import com.example.api.group.model.Group;
import com.example.api.map.model.Chapter;
import com.example.api.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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
    private List<Group> groups = new LinkedList<>();

    @OneToMany(mappedBy = "course")
    @JsonBackReference
    private List<Chapter> chapters = new LinkedList<>();

    public Course(Long id, String name, String description, Boolean isArchived, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isArchived = isArchived;
        this.owner = owner;
    }
}
