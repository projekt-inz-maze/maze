package com.example.api.file.image;

import com.example.api.course.Course;
import com.example.api.file.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image extends File {
    private ImageType type;

    public Image(String name, byte[] file, ImageType type, Course course) {
        super(null, name, course, file);
        this.type = type;
    }
}
