package com.example.rentvideo.repository;

import com.example.rentvideo.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
