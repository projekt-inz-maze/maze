package com.example.api.util.repository;

import com.example.api.util.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findImageById(Long id);
}
