package com.wc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wc.forms.RatingForm;
import com.wc.repository.PMSRepository;

@Service
public class PerformanceManageService {

    private final PMSRepository ratingRepository;

    @Autowired
    public PerformanceManageService(PMSRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public void saveRating(RatingForm ratingForm) {
        // Convert RatingForm to Rating entity
        RatingForm rating = new RatingForm();
        rating.setUserId(ratingForm.getUserId());
        rating.setMonth(ratingForm.getMonth());
        rating.setYear(ratingForm.getYear());
        rating.setQuestion1(ratingForm.getQuestion1());
        rating.setQuestion2(ratingForm.getQuestion2());
        rating.setQuestion3(ratingForm.getQuestion3());
        rating.setQuestion4(ratingForm.getQuestion4());
        rating.setQuestion5(ratingForm.getQuestion5());
        // ... set other fields

        // Save the Rating entity to the repository
        ratingRepository.save(rating);
    }

	public String getFirstNameById(int userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<RatingForm> getUserRatings(String userId) {
        // Implement the logic to retrieve user ratings from the database
        // Example: return ratingRepository.findByUserId(userId);
        // Make sure to replace the example with your actual repository method
        return null;
    }

    // Add more methods as needed
}