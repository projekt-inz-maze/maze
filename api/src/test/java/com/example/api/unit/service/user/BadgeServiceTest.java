package com.example.api.unit.service.user;

import com.example.api.course.coursemember.CourseMember;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.security.LoggedInUserService;
import com.example.api.user.model.AccountType;
import com.example.api.user.model.User;
import com.example.api.user.badge.types.ActivityNumberBadge;
import com.example.api.user.badge.types.Badge;
import com.example.api.user.badge.types.TopScoreBadge;
import com.example.api.user.badge.unlockedbadge.UnlockedBadge;
import com.example.api.user.badge.BadgeRepository;
import com.example.api.user.badge.unlockedbadge.UnlockedBadgeRepository;
import com.example.api.file.FileRepository;
import com.example.api.user.badge.BadgeService;
import com.example.api.user.service.UserService;
import com.example.api.validator.BadgeValidator;
import com.example.api.user.badge.BadgeVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class BadgeServiceTest {
    private BadgeService badgeService;
    @Mock private BadgeRepository badgeRepository;
    @Mock private UnlockedBadgeRepository unlockedBadgeRepository;
    @Mock private UserService userService;
    @Mock private BadgeVisitor badgeVisitor;
    @Mock private FileRepository fileRepository;
    @Mock private BadgeValidator badgeValidator;
    @Mock private LoggedInUserService authService;

    private User user;
    List<Badge> badges = new LinkedList<>();
    TopScoreBadge topScoreBadge;
    ActivityNumberBadge activityNumberBadge;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        badgeService = new BadgeService(
                badgeRepository,
                unlockedBadgeRepository,
                fileRepository,
                userService,
                badgeValidator,
                badgeVisitor,
                null,
                null,
                null
        );

        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setPassword("password");
        user.setAccountType(AccountType.STUDENT);

        topScoreBadge = new TopScoreBadge();
        activityNumberBadge = new ActivityNumberBadge();
        badges.add(topScoreBadge);
        badges.add(activityNumberBadge);
    }

    @Test
    public void checkAllBadgesWhenNoUnlockedBadges() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        CourseMember member = new CourseMember();
        when(authService.getCurrentUser()).thenReturn(user);
        when(badgeVisitor.visitTopScoreBadge(topScoreBadge)).thenReturn(true);
        when(badgeVisitor.visitActivityNumberBadge(activityNumberBadge)).thenReturn(false);
        when(authService.getCurrentUser()).thenReturn(user);
        doReturn(badges).when(badgeRepository).findAll();

        //when
        badgeService.checkAllBadges(member);

        //then
        List<UnlockedBadge> unlockedBadges = member.getUnlockedBadges();
        List<Badge> badgesInUnlockedBadges = unlockedBadges.stream()
                .map(UnlockedBadge::getBadge)
                .toList();
        assertThat(unlockedBadges).hasSize(1);
        assertThat(badgesInUnlockedBadges).contains(topScoreBadge);
    }

    @Test
    public void checkAllBadgesWhenHasUnlockedBadges() throws WrongUserTypeException, EntityNotFoundException, MissingAttributeException {
        //given
        CourseMember member = new CourseMember();
        UnlockedBadge unlockedBadge = new UnlockedBadge();
        unlockedBadge.setBadge(topScoreBadge);
        member.getUnlockedBadges().add(unlockedBadge);
        when(authService.getCurrentUser()).thenReturn(user);
        when(badgeVisitor.visitTopScoreBadge(topScoreBadge)).thenReturn(true);
        when(badgeVisitor.visitActivityNumberBadge(activityNumberBadge)).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(user);
        doReturn(badges).when(badgeRepository).findAll();

        //when
        badgeService.checkAllBadges(member);

        //then
        List<UnlockedBadge> unlockedBadges = member.getUnlockedBadges();
        List<Badge> badgesInUnlockedBadges = unlockedBadges.stream()
                .map(UnlockedBadge::getBadge)
                .toList();
        assertThat(unlockedBadges).hasSize(2);
        assertThat(badgesInUnlockedBadges).contains(activityNumberBadge, topScoreBadge);
    }
}
