package com.example.api.chapter.response;

import com.example.api.chapter.Chapter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterResponseStudent extends ChapterResponse{
    private Boolean isFulfilled;

    public ChapterResponseStudent(Chapter chapter, Boolean isFulfilled) {
        super(chapter);
        this.isFulfilled = isFulfilled;
    }
}
