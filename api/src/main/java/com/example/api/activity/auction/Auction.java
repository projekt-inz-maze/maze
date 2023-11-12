package com.example.api.activity.auction;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityType;
import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.task.Task;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.course.Course;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Auction extends Activity {
    @NotNull private final ActivityType activityType = ActivityType.AUCTION;
    @NotNull private boolean resolved = false;
    @NotNull private Double minBidding;
    @NotNull private Double maxBidding;
    @NotNull private Double minScoreToGetPoints;
    @NotNull private LocalDateTime resolutionDate;
    @OneToOne @NotNull private Task task;
    @OneToOne @NotNull private Bid highestBid;

    public Auction(Long id,
                   String title,
                   String description,
                   Integer posX,
                   Integer posY,
                   Double experience,
                   Long creationTime,
                   List<Requirement> requirements,
                   Boolean isBlocked,
                   User professor,
                   Course course,
                   Task task,
                   Double minBidding,
                   Double maxBidding,
                   LocalDateTime resolutionDate,
                   Double minScoreToGetPoints) {
        super(id,
                title,
                description,
                posX,
                posY,
                experience,
                creationTime,
                requirements,
                isBlocked,
                professor,
                ActivityType.AUCTION,
                course);
        this.task = task;
        this.minBidding = minBidding;
        this.maxBidding = maxBidding;
        this.resolutionDate = resolutionDate;
        this.minScoreToGetPoints = minScoreToGetPoints;
    }

    public Optional<Bid> getHighestBid() {
        return  Optional.ofNullable(highestBid);
    }

    public Double currentMinBiddingValue() {
        return  getHighestBid().map(Bid::getPoints).orElse(minBidding);
    }

    @Override
    public Double getMaxPoints() {
        return task.getMaxPoints();
    }

    public static AuctionBuilder from(Task task) {
        return AuctionBuilder.from(task);
    }

    public static class AuctionBuilder {
        private Long id;
        private String title;
        private String description;
        private Integer posX;
        private Integer posY;
        private Double experience;
        private Long creationTime;
        private List<Requirement> requirements;
        private Boolean isBlocked;
        private User professor;
        private Course course;
        private Task task;
        private Double minBidding;
        private Double maxBidding;
        private LocalDateTime resolutionDate;
        private Double minScoreToGetPoints;

        AuctionBuilder() {
        }

        public static AuctionBuilder from(Task task) {
            return new AuctionBuilder()
                    .title("Licytacja - " + task.getTitle())
                    .description(task.getDescription())
                    .posX(task.getPosX())
                    .posY(task.getPosY())
                    .experience(task.getExperience())
                    .creationTime(task.getCreationTime())
                    .isBlocked(task.getIsBlocked())
                    .professor(task.getProfessor())
                    .course(task.getCourse())
                    .task(task);
        }

        public AuctionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuctionBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AuctionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AuctionBuilder posX(Integer posX) {
            this.posX = posX;
            return this;
        }

        public AuctionBuilder posY(Integer posY) {
            this.posY = posY;
            return this;
        }

        public AuctionBuilder experience(Double experience) {
            this.experience = experience;
            return this;
        }

        public AuctionBuilder creationTime(Long creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public AuctionBuilder requirements(List<Requirement> requirements) {
            this.requirements = requirements;
            return this;
        }

        public AuctionBuilder isBlocked(Boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public AuctionBuilder professor(User professor) {
            this.professor = professor;
            return this;
        }

        public AuctionBuilder minScoreToGetPoints(Double minScoreToGetPoints) {
            this.minScoreToGetPoints = minScoreToGetPoints;
            return this;
        }

        public AuctionBuilder course(Course course) {
            this.course = course;
            return this;
        }

        public AuctionBuilder task(Task task) {
            this.task = task;
            return this;
        }

        public AuctionBuilder minBidding(Double minBidding) {
            this.minBidding = minBidding;
            return this;
        }

        public AuctionBuilder maxBidding(Double maxBidding) {
            this.maxBidding = maxBidding;
            return this;
        }

        public AuctionBuilder resolutionDate(LocalDateTime resolutionDate) {
            this.resolutionDate = resolutionDate;
            return this;
        }

        public Auction build() {
            return new  Auction( id,
                    title,
                    description,
                    posX,
                    posY,
                    experience,
                    creationTime,
                    requirements,
                    isBlocked,
                    professor,
                    course,
                    task,
                    minBidding,
                    maxBidding,
                    resolutionDate,
                    minScoreToGetPoints);
        }
    }
}
