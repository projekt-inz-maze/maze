package com.example.api.map.mapactivity;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityType;
import com.example.api.chapter.requirement.RequirementResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapActivityStudent extends MapActivity {
    private Boolean isFulfilled;
    private Boolean isCompleted;
    private Double awardedPoints;


    public MapActivityStudent(Long id,
                              Integer posX,
                              Integer posY,
                              ActivityType type,
                              String title,
                              Double points,
                              Long creationTime,
                              String description,
                              RequirementResponse requirements,
                              Boolean isFulfilled,
                              Boolean isCompleted,
                              Double awardedPoint) {
     super(id, posX, posY, type, title, points, creationTime, description, requirements);
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;
        this.awardedPoints = awardedPoint;
    }

    public MapActivityStudent(Activity activity, Boolean isFulfilled, Boolean isCompleted, RequirementResponse requirements, double points) {
        super(activity.getId(),
                activity.getPosX(),
                activity.getPosY(),
                activity.getActivityType(),
                activity.getTitle(),
                activity.getMaxPoints(),
                activity.getCreationTime(),
                activity.getDescription(),
                requirements);
        this.isFulfilled = isFulfilled;
        this.isCompleted = isCompleted;
    }

    public static MapActivityStudentBuilder builder() {
        return new MapActivityStudentBuilder();
    }

    public static class MapActivityStudentBuilder {
        private Long id;
        private Integer posX;
        private Integer posY;
        private ActivityType type;
        private String title;
        private Double points;
        private Long creationTime;
        private String description;
        private RequirementResponse requirements;

        private Boolean isFulfilled;
        private Boolean isCompleted;
        private Double awardedPoints;

        MapActivityStudentBuilder() {
        }

        public MapActivityStudentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MapActivityStudentBuilder posX(Integer posX) {
            this.posX = posX;
            return this;
        }

        public MapActivityStudentBuilder posY(Integer posY) {
            this.posY = posY;
            return this;
        }

        public MapActivityStudentBuilder type(ActivityType type) {
            this.type = type;
            return this;
        }

        public MapActivityStudentBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MapActivityStudentBuilder points(Double points) {
            this.points = points;
            return this;
        }

        public MapActivityStudentBuilder creationTime(Long creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public MapActivityStudentBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MapActivityStudentBuilder requirements(RequirementResponse requirements) {
            this.requirements = requirements;
            return this;
        }

        public MapActivityStudentBuilder isFulfilled(boolean isFulfilled) {
            this.isFulfilled = isFulfilled;
            return this;
        }

        public MapActivityStudentBuilder isCompleted(boolean isCompleted) {
            this.isCompleted = isCompleted;
            return this;
        }


        public MapActivityStudentBuilder awardedPoints(Double awardedPoints) {
            this.awardedPoints = awardedPoints;
            return this;
        }

        public MapActivityStudentBuilder fromActivity(Activity activity) {
            this.id = activity.getId();
            this.posX = activity.getPosX();
            this.posY = activity.getPosY();
            this.type = activity.getActivityType();
            this.title = activity.getTitle();
            this.points = activity.getMaxPoints();
            this.creationTime = activity.getCreationTime();
            this.description = activity.getDescription();
            return this;
        }

        public MapActivityStudent build() {
            return new MapActivityStudent(this.id,
                    this.posX,
                    this.posY,
                    this.type,
                    this.title,
                    this.points,
                    this.creationTime,
                    this.description,
                    this.requirements,
                    this.isFulfilled,
                    this.isCompleted,
                    this.awardedPoints);
        }

        public String toString() {
            return "MapActivity.MapActivityBuilder(id=" + this.id + ", posX=" + this.posX + ", posY=" + this.posY + ", type=" + this.type + ", title=" + this.title + ", points=" + this.points + ", creationTime=" + this.creationTime + ", description=" + this.description + ", requirements=" + this.requirements + ")";
        }
    }
}
