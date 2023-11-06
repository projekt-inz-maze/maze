package com.example.api.activity.auction;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityType;
import com.example.api.activity.auction.bid.Bid;
import com.example.api.activity.task.Task;
import com.example.api.chapter.requirement.model.Requirement;
import com.example.api.course.model.Course;
import com.example.api.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/*

1.1 schedule auction resolve
1.2 zablokowanie odblokowania Tasku, który ma Auction done
przekazywanie auction do mapyyy
2. dodanie Biddingu
3. dodac do endpointa /statistics
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Auction extends Activity {
    private final ActivityType activityType = ActivityType.AUCTION;
    @OneToOne
    private Task task;
    private Double minBidding;
    private Instant resolutionDate;
    private boolean resolved = false;
    @OneToOne
    private Bid highestBid;

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
                   Instant resolutionDate) {
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
        this.resolutionDate = resolutionDate;
    }

    public Optional<Bid> getHighestBid() {
        return  Optional.ofNullable(highestBid);
    }

    public Double currentMinBiddingValue() {
        return  getHighestBid().map(Bid::getValue).orElse(minBidding);
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
        private ActivityType activityType;
        private Course course;
        private Task task;
        private Double minBidding;
        private Instant resolutionDate;

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

        public AuctionBuilder activityType(ActivityType activityType) {
            this.activityType = activityType;
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

        public AuctionBuilder resolutionDate(Instant resolutionDate) {
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
                    resolutionDate);
        }
    }
}
