package com.example.api.util.dto.response;

import com.example.api.util.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ByteFileResponse {
    private Long fileId;
    private String name;
    private byte[] file;

    public ByteFileResponse(File file) {
        this.fileId = file.getId();
        this.name = file.getName();
        this.file = file.getFile();
    }
}
