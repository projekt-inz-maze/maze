package com.example.api.activity.info;

import com.example.api.file.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InfoResponse {
    private String name;
    private String description;
    private List<String> imageUrls;
    private String content;
    private List<FileResponse> files;
}
