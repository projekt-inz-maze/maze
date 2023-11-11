package com.example.api.user.model;

import com.example.api.course.Course;
import com.example.api.user.hero.HeroType;
import com.example.api.util.model.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private HeroType heroType;
    private String name;
    private Double minPoints;

    @OneToOne
    private Image image;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
