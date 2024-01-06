package com.example.api.activity.submittask.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitTaskResultWithFileDTO {
    @Schema(required = true) private Long id;
    @Schema(required = true) private String title;
    @Schema(required = true) private String content;
    @Schema(required = false) private MultipartFile file;
    @Schema(required = false) private String fileName;
}
