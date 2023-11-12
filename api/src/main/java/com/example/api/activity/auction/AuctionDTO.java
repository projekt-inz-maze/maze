package com.example.api.activity.auction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDTO {
    @Schema(required = true) private Long id;
    @Schema(required = true) private Double minBidding;
    @Schema(required = true) private Double maxBidding;
    @Schema(required = true) private Optional<Double> userBid;
    @Schema(required = true) private Long endDateEpochSeconds;
    @Schema(required = true) private Double minScoreToGetPoints;
}
