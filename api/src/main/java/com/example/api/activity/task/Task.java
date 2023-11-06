package com.example.api.activity.task;

import com.example.api.activity.Activity;
import com.example.api.activity.auction.Auction;
import com.example.api.course.model.Course;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Task extends Activity {

    @NotNull
    private String requiredKnowledge;

    @NotNull
    private Double maxPoints;

    @OneToOne
    private Auction auction;

    public Task(String name,
                String description,
                int posX,
                int posY,
                User professor,
                String requiredKnowledge,
                Double maxPoints,
                Course course){
        super(name, description, posX, posY, professor, course);
        this.requiredKnowledge = requiredKnowledge;
        this.maxPoints = maxPoints;
    }

    public Optional<Auction> getAuction() {
        return Optional.ofNullable(auction);
    }
}
