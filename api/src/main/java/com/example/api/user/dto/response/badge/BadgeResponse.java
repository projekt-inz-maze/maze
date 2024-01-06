package com.example.api.user.dto.response.badge;

import com.example.api.file.ByteFileResponse;
import com.example.api.user.badge.types.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadgeResponse<T> {
    private Long id;
    private String title;
    private String description;
    private ByteFileResponse file;
    private T value;

    public BadgeResponse(Badge badge) {
        this.id = badge.getId();
        this.title = badge.getTitle();
        this.description = badge.getDescription();
        this.file = new ByteFileResponse(badge.getImage());
    }
}
