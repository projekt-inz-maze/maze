package com.example.api.user.dto.response;

import com.example.api.course.coursemember.CourseMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicStudent {
    @Schema(required = true) private Long id;
    @Schema(required = true) private String firstName;
    @Schema(required = true) private String lastName;
    @Schema(required = true) private String groupName;

    public BasicStudent(CourseMember courseMember) {
        this.id = courseMember.getUser().getId();
        this.firstName = courseMember.getUser().getFirstName();
        this.lastName = courseMember.getUser().getLastName();
        this.groupName = courseMember.getGroup().getName();
    }
}