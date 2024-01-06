package com.example.api.file;

import com.example.api.activity.Activity;
import com.example.api.activity.ActivityService;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.file.image.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {
    private final ImageRepository imageRepository;
    private final FileRepository fileRepository;
    private final ActivityService activityService;

    public List<ChapterImageResponse> getImagesForChapter() {

        log.info("Fetching all images for chapter");
        return imageRepository.findAllByType(ImageType.CHAPTER)
                .stream()
                .map(image -> new ChapterImageResponse(image.getId(), image.getName(), image.getType()))
                .toList();
    }

    public ImageDTO getImage(Long id) throws EntityNotFoundException {
        log.info("Fetching image with id {}", id);
        Image image = imageRepository.findImageById(id);
        if (image == null) {
            log.error("Image with given id {} was not found", id);
            throw new EntityNotFoundException("Image with given id " + id + " was not found");
        }
        return new ImageDTO(image);
    }

    public void addFile(ActivityFileDTO dto) throws EntityNotFoundException, IOException {
        Activity activity = activityService.getActivity(dto.getActivityId());
        File file = new File(null, dto.getFileName(), dto.getFile().getBytes());
        fileRepository.save(file);
        activityService.addFile(activity, file);
    }

    public ByteArrayResource getFileById(Long fileId) throws EntityNotFoundException {
        File file = Optional.ofNullable(fileRepository.findFileById(fileId)).orElseThrow(() -> new EntityNotFoundException("File not found"));
        return new ByteArrayResource(file.getFile());
    }
}
