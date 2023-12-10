package com.example.api.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ActivityFileDTO {
    @Schema(required = true) private Long activityId;
    @Schema(required = false) private MultipartFile file;
    @Schema(required = false) private String fileName;
}
