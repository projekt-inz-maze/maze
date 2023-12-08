package com.example.api.activity.task.dto.response.util;

import com.example.api.file.File;
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
