package com.example.api.file.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findImageById(Long id);
    List<Image> findAllByType(ImageType type);

}
