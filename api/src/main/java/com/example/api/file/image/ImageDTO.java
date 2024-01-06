package com.example.api.file.image;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Lob;

@Data
@Getter
@RequiredArgsConstructor
public class ImageDTO {
    private ImageType type;

    private Long id;
    private String name;

    @Lob
    private byte[] file;

    public ImageDTO(Image image) {
        this.type = image.getType();
        this.id = image.getId();
        this.name = image.getName();
        this.file = image.getFile();
    }
}
