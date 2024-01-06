package com.example.api.user.badge.unlockedbadge;

import com.example.api.course.coursemember.CourseMember;
import com.example.api.user.badge.types.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UnlockedBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Badge badge;
    private Long unlockedDateMillis;

    @ManyToOne
    @JoinColumn(name = "coursemember_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseMember courseMember;

    public UnlockedBadge(Badge badge, long unlockedDateMillis, CourseMember member) {
        this.badge = badge;
        this.unlockedDateMillis = unlockedDateMillis;
        this.courseMember = member;
    }
}
