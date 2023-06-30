package com.example.api.user.dto.response.grade;

import com.example.api.user.dto.response.BasicUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GradeResponse {
    private BasicUser student;
    private Double grade;
}
