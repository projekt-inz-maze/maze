package com.example.api.util.service;

import com.example.api.course.model.Course;
import com.example.api.course.service.CourseService;
import com.example.api.course.validator.CourseValidator;
import com.example.api.util.dto.response.ChapterImageResponse;
import com.example.api.error.exception.EntityNotFoundException;
import com.example.api.util.model.Image;
import com.example.api.util.model.ImageType;
import com.example.api.util.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {
    private final ImageRepository imageRepository;
    private final CourseService courseService;
    private final CourseValidator courseValidator;

    public List<ChapterImageResponse> getImagesForChapter(Long courseId) throws EntityNotFoundException {
        courseValidator.validateCurrentUserCanAccess(courseId);
        Course course = courseService.getCourse(courseId);
        log.info("Fetching all images for chapter");
        return imageRepository.findAllByCourse(course)
                .stream()
                .filter(image -> image.getType() == ImageType.CHAPTER)
                .map(image -> new ChapterImageResponse(image.getId(), image.getName(), image.getType()))
                .toList();
    }

    public Image getImage(Long id) throws EntityNotFoundException {
        log.info("Fetching image with id {}", id);
        Image image = imageRepository.findImageById(id);
        if (image == null) {
            log.error("Image with given id {} was not found", id);
            throw new EntityNotFoundException("Image with given id " + id + " was not found");
        }
        return image;
    }
}
