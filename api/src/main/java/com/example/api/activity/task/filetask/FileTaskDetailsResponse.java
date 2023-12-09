package com.example.api.activity.task.filetask;

import com.example.api.file.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTaskDetailsResponse {
    private Long fileTaskId;
    private String name;
    private String description;
    private String answer;
    private List<FileResponse> taskFiles; //files posted by the student
    private Double points;
    private String remarks;
    private FileResponse feedbackFile;
    private List<FileResponse> files;
}
