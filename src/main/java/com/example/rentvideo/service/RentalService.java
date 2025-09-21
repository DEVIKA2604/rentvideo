package com.example.rentvideo.service;

import com.example.rentvideo.model.Rental;
import com.example.rentvideo.model.User;
import com.example.rentvideo.model.Video;
import com.example.rentvideo.repository.RentalRepository;
import com.example.rentvideo.repository.UserRepository;
import com.example.rentvideo.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         VideoRepository videoRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public List<Rental> getActiveRentals(User user) {
        return rentalRepository.findByUserAndReturnedAtIsNull(user);
    }

    @Transactional
    public Rental rentVideo(User user, Video video) {
        // Check video availability
        if (!video.isAvailable()) {
            throw new RuntimeException("Video not available for rent");
        }

        // Check max 2 active rentals
        List<Rental> activeRentals = rentalRepository.findByUserAndReturnedAtIsNull(user);
        if (activeRentals.size() >= 2) {
            throw new RuntimeException("Maximum 2 active rentals allowed");
        }

        // Rent video
        video.setAvailable(false);
        videoRepository.save(video);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVideo(video);
        rental.setRentedAt(LocalDateTime.now());
        rental.setReturnedAt(null);

        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental returnVideo(User user, Video video) {
        Optional<Rental> rentalOpt = rentalRepository.findByUserAndVideoAndReturnedAtIsNull(user, video);
        if (rentalOpt.isEmpty()) {
            throw new RuntimeException("No active rental found for this video");
        }

        Rental rental = rentalOpt.get();
        rental.setReturnedAt(LocalDateTime.now());

        video.setAvailable(true);
        videoRepository.save(video);

        return rentalRepository.save(rental);
    }
}
