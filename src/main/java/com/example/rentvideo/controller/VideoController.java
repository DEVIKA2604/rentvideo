package com.example.rentvideo.controller;

import com.example.rentvideo.model.Video;
import com.example.rentvideo.repository.VideoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoRepository videoRepository;

    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    @GetMapping
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

  
    @GetMapping("/available")
    public List<Video> getAvailableVideos() {
        return videoRepository.findByAvailableTrue();
    }


    @PostMapping("/manage/add")
    public Video addVideo(@RequestBody Video video) {
        video.setAvailable(true); // assume available by default
        return videoRepository.save(video);
    }

  
    @PutMapping("/manage/{id}")
    public Video updateVideo(@PathVariable Long id, @RequestBody Video updatedVideo) {
        return videoRepository.findById(id)
                .map(video -> {
                    video.setTitle(updatedVideo.getTitle());
                    video.setDirector(updatedVideo.getDirector());
                    video.setGenre(updatedVideo.getGenre());
                    video.setAvailable(updatedVideo.isAvailable());
                    return videoRepository.save(video);
                })
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }


    @DeleteMapping("/manage/{id}")
    public String deleteVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found");
        }
        videoRepository.deleteById(id);
        return "Video deleted successfully";
    }
}
