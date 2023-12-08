package com.example.api.activity;

import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.course.Course;
import com.example.api.file.File;
import com.example.api.user.model.User;
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
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Activity {
    @TableGenerator(name = "myGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "NEXT_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "myGen")
    @Id
    private Long id;
    private String title;
    private String description;
    private Integer posX;
    private Integer posY;
    private Double experience;
    private Long creationTime = System.currentTimeMillis();

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<File> files = new LinkedList<>();

    @OneToMany
    private List<Requirement> requirements = new LinkedList<>();
    private Boolean isBlocked = true;

    @OneToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User professor;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double maxPoints;

    public Activity(String name, String description, int posX, int posY, User professor, Course course) {
        this.title = name;
        this.description = description;
        this.posX = posX;
        this.posY = posY;
        this.professor = professor;
        this.course = course;
    }

    public Activity(String name, String description, int posX, int posY, User professor, Course course, Double maxPoints) {
        this(name, description, posX, posY, professor, course);
        this.maxPoints = maxPoints;
    }

    public void addFile(File file) {
        files.add(file);
    }
}
