package com.example.api.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String name;

    public FileResponse(File file) {
        this.id = file.getId();
        this.name = file.getName();
    }
}
