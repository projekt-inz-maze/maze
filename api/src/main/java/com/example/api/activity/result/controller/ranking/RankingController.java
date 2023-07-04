package com.example.api.activity.result.controller.ranking;

import com.example.api.ranking.dto.response.RankingResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.MissingAttributeException;
import com.example.api.error.exception.WrongUserTypeException;
import com.example.api.activity.result.service.ranking.RankingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
@SecurityRequirement(name = "JWT_AUTH")
public class RankingController {
    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingResponse>> getRanking(@RequestParam Long courseId) {
        return ResponseEntity.ok().body(rankingService.getRanking());
    }

    @GetMapping("/group")
    public ResponseEntity<List<RankingResponse>> getRankingForGroup()
            throws EntityNotFoundException {
        return ResponseEntity.ok().body(rankingService.getRankingForLoggedStudentGroup());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RankingResponse>> getSearchedRanking(@RequestParam Long courseId, @RequestParam String search) {
        return ResponseEntity.ok().body(rankingService.getSearchedRanking(search));
    }

    @GetMapping("/position")
    public ResponseEntity<Integer> getRankingPosition(@RequestParam Long courseId)
            throws WrongUserTypeException {
        return ResponseEntity.ok().body(rankingService.getRankingPosition());
    }

    @GetMapping("/group/position")
    public ResponseEntity<Integer> getGroupRankingPosition(@RequestParam Long courseId)
            throws WrongUserTypeException, MissingAttributeException, UsernameNotFoundException, EntityNotFoundException {
        return ResponseEntity.ok().body(rankingService.getGroupRankingPosition());
    }

    @GetMapping("/activity")
    public ResponseEntity<List<RankingResponse>> getAllPointsActivityList(@RequestParam Long activityID) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(rankingService.getActivityRanking(activityID));
    }

    @GetMapping("/activity/search")
    public ResponseEntity<List<RankingResponse>> getAllPointsActivityListSearch(
            @RequestParam Long courseId,
            @RequestParam Long activityID,
            @RequestParam String search
    ) throws WrongUserTypeException, EntityNotFoundException {
        return ResponseEntity.ok().body(rankingService.getActivityRankingSearch(activityID, search));
    }
}