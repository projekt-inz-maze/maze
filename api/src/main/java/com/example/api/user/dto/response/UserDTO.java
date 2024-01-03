package com.example.api.user.dto.response;

import com.example.api.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    @Schema(required = true) private final Long id;
    @Schema(required = true) private final String firstName;
    @Schema(required = true) private final String lastName;
    @Schema(required = true) private final Integer indexNumber;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.indexNumber = user.getIndexNumber();
    }
}
