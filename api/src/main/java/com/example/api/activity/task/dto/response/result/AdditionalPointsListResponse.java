package com.example.api.activity.task.dto.response.result;

import com.example.api.activity.ActivityType;
import lombok.Data;

@Data
public class AdditionalPointsListResponse extends PointsResponse {

    public AdditionalPointsListResponse(AdditionalPointsResponse additionalPointsResponse) {
        super(
                additionalPointsResponse.getDateMillis(),
                additionalPointsResponse.getPoints(),
                ActivityType.ADDITIONAL,
                additionalPointsResponse.getDescription()
        );
    }
}
