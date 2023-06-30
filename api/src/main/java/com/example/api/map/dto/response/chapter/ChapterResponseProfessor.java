package com.example.api.map.dto.response.chapter;

import com.example.api.map.model.Chapter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterResponseProfessor extends ChapterResponse{
    private Boolean isBlocked;

    public ChapterResponseProfessor(Chapter chapter) {
        super(chapter);
        this.isBlocked = chapter.getIsBlocked();
    }
}
