package com.example.rentvideo.repository;

import com.example.rentvideo.model.Rental;
import com.example.rentvideo.model.User;
import com.example.rentvideo.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserAndReturnedAtIsNull(User user); // active rentals for a user

    Optional<Rental> findByUserAndVideoAndReturnedAtIsNull(User user, Video video); // active rental for a specific video
}
