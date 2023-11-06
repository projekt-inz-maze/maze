package com.example.api.util.repository;

import com.example.api.util.model.Image;
import com.example.api.util.model.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findImageById(Long id);
    List<Image> findAllByType(ImageType type);

}
