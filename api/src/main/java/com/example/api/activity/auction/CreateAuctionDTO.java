package com.example.api.activity.auction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAuctionDTO {
    @Schema(required = true) private Double minBidding;
    @Schema(required = true) private Long resolutionDate;
    @Schema(required = true) private Double minScoreToGetPoints;
}
