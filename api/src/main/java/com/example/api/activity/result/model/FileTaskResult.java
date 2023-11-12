package com.example.api.activity.result.model;

import com.example.api.activity.Activity;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.util.model.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileTaskResult extends TaskResult {
    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<File> files = new LinkedList<>();

    @Nullable
    @Lob
    private String answer;

    public boolean isEvaluated;

    public FileTask getFileTask() {
        return (FileTask) activity;
    }

    @Override
    public boolean isEvaluated() {
        return this.getPoints() != null;
    }
}
