package com.example.api.util.controller;

import com.example.api.util.dto.response.ChapterImageResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.util.model.Image;
import com.example.api.util.service.FileService;
import com.example.api.util.RequestInterceptor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@SecurityRequirement(name = "JWT_AUTH")
public class FileController {
    private final FileService fileService;

    @GetMapping("/chapter/images")
    public ResponseEntity<List<ChapterImageResponse>> getImagesForChapter(@RequestParam Long courseId) throws EntityNotFoundException {
        return ResponseEntity.ok().body(fileService.getImagesForChapter(courseId));
    }

    @GetMapping
    public ResponseEntity<Image> getImage(@RequestParam Long id) throws EntityNotFoundException {
        return ResponseEntity.ok().body(fileService.getImage(id));
    }

    @GetMapping("/log")
    public ResponseEntity<ByteArrayResource> getLogFile() throws IOException {
        File file = new File(RequestInterceptor.REQUEST_LOGGING_FILE_PATH);
        file.createNewFile();
        return ResponseEntity.ok().body(new ByteArrayResource(Files.readAllBytes(file.toPath())));
    }
}

