package com.example.api.activity.task.dto.request.create;

import com.example.api.map.dto.response.task.ActivityType;
import com.example.api.activity.task.model.Info;
import com.example.api.util.model.Url;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInfoForm extends CreateActivityForm{
    @Schema(required = true) private List<String> imageUrls;
    @Schema(required = true) private String infoContent;

    public CreateInfoForm(String title,
                          String description,
                          Integer posX,
                          Integer posY,
                          List<String> imageUrls,
                          String content){
        super(ActivityType.INFO, title, description, posX, posY);
        this.imageUrls = imageUrls;
        this.infoContent = content;
    }

    public CreateInfoForm(Info info) {
        super(info);
        this.imageUrls = info.getImageUrls().stream().map(Url::getUrl).toList();
        this.infoContent = info.getContent();
    }
}
