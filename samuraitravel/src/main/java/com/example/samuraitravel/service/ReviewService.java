package com.example.samuraitravel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired
	private final ReviewRepository reviewRepository;
    
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository; 
    } 
    
    public Review findReviewById(Integer id) {
    	return reviewRepository.findReviewById(id);
    }
    
    public Page<Review> findReviewsByHouseOrderByCreatedAtDesc(Integer houseId, Pageable pageable) {
    	return reviewRepository.findReviewsByHouseOrderByCreatedAtDesc(houseId, pageable);
    }
    
    public long countReviews() {
    	return reviewRepository.count();
    }
    
    public Review findFirstReviewByOrderByIdDesc() {
    	return reviewRepository.findFirstReviewByOrderByIdDesc();
    }
    
    public Review createReview(String content, int rating, User user, House house) {
    	Review review = new Review();
    	review.setContent(content);
    	review.setRating(rating);
    	review.setUser(user);
    	review.setHouse(house);
    	return reviewRepository.save(review);
    }
    
    public Review updateReview(Integer reviewId, String newContent, int newRating) {
    	
    	Review review = reviewRepository.findReviewById(reviewId);
    		review.setContent(newContent);
        	review.setRating(newRating);
        	return reviewRepository.save(review); 
    }
 
    public void deleteReview(Integer id) {
    	reviewRepository.deleteById(id);
    }
    
    public boolean hasUserAlreadyReviewed(House house, User user) {
    	Review review = reviewRepository.findByHouseAndUser(house, user); {
    		if(review == null) {
    			return false;
    		} else {
    			return true;
    		}
    	}
    }
    
    //指定した民宿に関連するレビューが存在するか
    public boolean existsReviewsForHouse(House house) {
    	return reviewRepository.existsByHouse(house);
    }
   

}
