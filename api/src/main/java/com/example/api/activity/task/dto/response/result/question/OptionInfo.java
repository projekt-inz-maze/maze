package com.example.api.activity.task.dto.response.result.question;

import com.example.api.question.model.Option;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OptionInfo {
    private Long id;
    private String content;

    public OptionInfo(Option option) {
        this.id = option.getId();
        this.content = option.getContent();
    }
}
