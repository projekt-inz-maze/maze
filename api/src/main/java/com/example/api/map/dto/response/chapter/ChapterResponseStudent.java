package com.example.api.map.dto.response.chapter;

import com.example.api.map.model.Chapter;
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
