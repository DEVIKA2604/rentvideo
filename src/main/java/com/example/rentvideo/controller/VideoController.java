package com.example.rentvideo.controller;

import com.example.rentvideo.model.User;
import com.example.rentvideo.model.Video;
import com.example.rentvideo.service.RentalService;
import com.example.rentvideo.service.UserService;
import com.example.rentvideo.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;
    private final RentalService rentalService;
    private final UserService userService;

    public VideoController(VideoService videoService,
                           RentalService rentalService,
                           UserService userService) {
        this.videoService = videoService;
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    @PostMapping
    public ResponseEntity<?> createVideo(@RequestBody Video video, Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (!user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).body("Only ADMIN can create videos");
        }
        videoService.saveVideo(video);
        return ResponseEntity.ok(video);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVideo(@PathVariable Long id,
                                         @RequestBody Video updatedVideo,
                                         Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (!user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).body("Only ADMIN can update videos");
        }

        Optional<Video> optVideo = videoService.getVideoById(id);
        if (optVideo.isEmpty()) return ResponseEntity.notFound().build();

        Video video = optVideo.get();
        video.setTitle(updatedVideo.getTitle());
        video.setDirector(updatedVideo.getDirector());
        video.setGenre(updatedVideo.getGenre());

        videoService.saveVideo(video);
        return ResponseEntity.ok(video);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (!user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).body("Only ADMIN can delete videos");
        }

        Optional<Video> optVideo = videoService.getVideoById(id);
        if (optVideo.isEmpty()) return ResponseEntity.notFound().build();

        videoService.deleteVideo(id);
        return ResponseEntity.ok("Video deleted successfully");
    }

    @PostMapping("/{id}/rent")
    public ResponseEntity<?> rentVideo(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        if (!user.getRole().name().equals("CUSTOMER")) {
            return ResponseEntity.status(403).body("Only CUSTOMER can rent videos");
        }

        Optional<Video> optVideo = videoService.getVideoById(id);
        if (optVideo.isEmpty()) return ResponseEntity.notFound().build();

        try {
            rentalService.rentVideo(user, optVideo.get());
            return ResponseEntity.ok("Video rented successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnVideo(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();

        Optional<Video> optVideo = videoService.getVideoById(id);
        if (optVideo.isEmpty()) return ResponseEntity.notFound().build();

        try {
            rentalService.returnVideo(user, optVideo.get());
            return ResponseEntity.ok("Video returned successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }
}
