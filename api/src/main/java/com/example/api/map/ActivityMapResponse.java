package com.example.api.map;

import com.example.api.map.mapactivity.MapActivity;
import com.example.api.util.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActivityMapResponse {
    private Long id;
    private List<? extends MapActivity> tasks;
    private Integer mapSizeX;
    private Integer mapSizeY;
    private File image;
}
