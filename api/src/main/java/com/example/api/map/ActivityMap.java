package com.example.api.map;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.activity.info.Info;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.util.model.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ActivityMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<GraphTask> graphTasks = new LinkedList<>();

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<FileTask> fileTasks = new LinkedList<>();

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Info> infos = new LinkedList<>();

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Survey> surveys = new LinkedList<>();

    @OneToMany
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Auction> auctions = new LinkedList<>();

    private Integer mapSizeX;
    private Integer mapSizeY;

    @OneToOne
    private File image;

    public ActivityMap(int mapSizeX, int mapSizeY, File image) {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.image = image;
    }

    public boolean hasActivity(Activity activity) {
        return Stream.of(graphTasks, fileTasks, infos, surveys)
                .flatMap(Collection::stream)
                .toList()
                .contains(activity);
    }

    public void add(Auction auction) {
        auctions.add(auction);
    }
}