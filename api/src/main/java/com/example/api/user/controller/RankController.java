package com.example.api.user.controller;

import com.example.api.user.dto.request.rank.AddRankForm;
import com.example.api.user.dto.request.rank.EditRankForm;
import com.example.api.user.dto.response.rank.CurrentRankResponse;
import com.example.api.user.dto.response.rank.RanksForHeroTypeResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.error.exception.RequestValidationException;
import com.example.api.user.service.RankService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
@SecurityRequirement(name = "JWT_AUTH")
public class RankController {
    private final RankService rankService;

    @GetMapping("/all")
    public ResponseEntity<List<RanksForHeroTypeResponse>> getAllRanks(@RequestParam Long courseId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(rankService.getAllRanks(courseId));
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addNewRank(@ModelAttribute AddRankForm form) throws RequestValidationException, IOException {
        rankService.addRank(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateRank(@ModelAttribute EditRankForm form) throws RequestValidationException, IOException {
        rankService.updateRank(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentRankResponse> getCurrentRankInfo(@RequestParam Long courseId) throws RequestValidationException {
        return ResponseEntity.ok().body(rankService.getCurrentRank(courseId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRank(@RequestParam Long rankId) throws RequestValidationException {
        rankService.deleteRank(rankId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
