package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	Page<Review> findByHouse(House house, Pageable pageable);
	Page<Review> findReviewsByHouseOrderByCreatedAtDesc(Integer houseId, Pageable pageable);
	public Review findFirstReviewByOrderByIdDesc();
	public Review findByHouseAndUser(House house, User user);
	public Review findReviewById(Integer id);
	boolean existsByHouse(House house);
	
}
