package com.example.api.map.dto.response;

import com.example.api.map.dto.response.task.MapTask;
import com.example.api.util.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActivityMapResponse {
    private Long id;
    private List<? extends MapTask> tasks;
    private Integer mapSizeX;
    private Integer mapSizeY;
    private File image;
}
