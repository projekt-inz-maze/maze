package com.example.api.map;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.activity.info.Info;
import com.example.api.activity.survey.Survey;
import com.example.api.activity.task.Task;
import com.example.api.activity.task.filetask.FileTask;
import com.example.api.activity.task.graphtask.GraphTask;
import com.example.api.activity.submittask.SubmitTask;
import com.example.api.file.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.action.internal.OrphanRemovalAction;
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

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<GraphTask> graphTasks = new LinkedList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<FileTask> fileTasks = new LinkedList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<Info> infos = new LinkedList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<Survey> surveys = new LinkedList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<Auction> auctions = new LinkedList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn
    private List<SubmitTask> submitTasks = new LinkedList<>();

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
        return getAllActivities().contains(activity);
    }

    public void add(Auction auction) {
        auctions.add(auction);
    }
    public void add(FileTask fileTask) {
        fileTasks.add(fileTask);
    }
    public void add(SubmitTask submitTask) {
        submitTasks.add(submitTask);
    }

    public List<? extends Activity> getAllActivities() {
        List<Task> tasks = Stream.concat(fileTasks.stream(), graphTasks.stream())
                .filter(task -> task.getAuction().map(Auction::isResolved).orElse(true))
                .toList();

        List<Auction> unresolvedAuctions = auctions.stream().filter(a -> !a.isResolved()).toList();

        return Stream.of(tasks, infos, surveys, unresolvedAuctions, submitTasks)
                .flatMap(Collection::stream)
                .toList();
    }

    public long getActivityCount() {
        return Stream.of(graphTasks, fileTasks, infos, surveys, submitTasks)
                .mapToLong(Collection::size)
                .sum();
    }


}