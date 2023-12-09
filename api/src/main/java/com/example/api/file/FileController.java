package com.example.api.file;

import com.example.api.file.image.ChapterImageResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.file.image.Image;
import com.example.api.util.RequestInterceptor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@SecurityRequirement(name = "JWT_AUTH")
public class FileController {
    private final FileService fileService;

    @GetMapping("/chapter/images")
    public ResponseEntity<List<ChapterImageResponse>> getImagesForChapter() {
        return ResponseEntity.ok().body(fileService.getImagesForChapter());
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

    @PostMapping(path = "/add", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addFileForActivity(@RequestBody ActivityFileDTO dto) throws EntityNotFoundException, IOException {
        fileService.addFile(dto);
        return ResponseEntity.ok().build();
    }
}

