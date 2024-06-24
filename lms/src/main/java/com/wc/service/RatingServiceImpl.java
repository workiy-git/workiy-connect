package com.wc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wc.repository.PMSRepository;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private PMSRepository ratingRepository; // Assuming you have a RatingRepository

    @Override
    public boolean hasRated(int userId, int year, String month) {
        // Implement logic to check if a rating already exists for the given user, year, and month
        // You can use your repository method or any other logic based on your data model
        // Return true if a rating exists, false otherwise
        return ratingRepository.existsByUserIdAndYearAndMonth(userId, year, month);
    }
}