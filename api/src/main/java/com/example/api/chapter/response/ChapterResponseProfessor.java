package com.example.api.chapter.response;

import com.example.api.chapter.Chapter;
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
