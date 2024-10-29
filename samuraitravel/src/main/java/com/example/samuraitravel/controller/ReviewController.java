package com.example.samuraitravel.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;
import com.example.samuraitravel.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/houses")
public class ReviewController {
	private final ReviewService reviewService;
	private HouseService houseService;
	private UserService userService;
	
	public ReviewController(ReviewService reviewService, HouseService houseService, UserService userService) {
		this.reviewService = reviewService;
		this.houseService = houseService;
		this.userService = userService;
	}

@GetMapping("/{houseId}/reviews")
public String index(@PathVariable(name = "houseId") Integer houseId, @PageableDefault(page = 0, size = 5, sort = "id", direction = Direction.ASC) Pageable pageable, Principal principal, Model model) {
	Page<Review> reviewPage = reviewService.findReviewsByHouseOrderByCreatedAtDesc(houseId, pageable);
	
	House house = houseService.findHouseById(houseId);
	if (house == null) {
		model.addAttribute("errorMessage", "民宿が存在しません。");
		return "redirect:/houses/index";
	}
	
	//ログイン中のユーザーのロール名を取得
	String userRoleName = userService.getUserRoleName(principal.getName());
	
	//ユーザーがレビューを投稿済みか確認
	User currentUser = userService.findByName(principal.getName());
	boolean hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(house, currentUser);
	
	model.addAttribute("house", house);
	model.addAttribute("userRoleName", userRoleName);
	model.addAttribute("reviewPage", reviewPage.getContent());
	model.addAttribute("page", reviewPage);
	model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
	
	return "reviews/index";
}

@GetMapping("/{houseId}/reviews/register")
public String register(@PathVariable(name = "houseId") Integer houseId, RedirectAttributes redirectAttributes, Principal principal, Model model) {
	House house = houseService.findHouseById(houseId);
	if (house == null) {
		model.addAttribute("errorMessage", "民宿が存在しません。");
		return "redirect:/houses/index";
	}
	
	model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
	
	return "reviews/register";
}

@GetMapping("/{houseId}/reviews/create")
public String create(@PathVariable(name = "houseId") Integer houseId, @ModelAttribute("reviewForm") @Valid ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult, Principal principal, Model model) {
	House house = houseService.findHouseById(houseId);
	if (house == null) {
		model.addAttribute("errorMessage", "民宿が存在しません。");
		return "redirect:/houses/index";
	}
	
	if (bindingResult.hasErrors()) {
		return "reviews/register";
	}
	
	 // 現在のユーザーを取得
    User user = userService.findByName(principal.getName());
	
	reviewService.createReview(reviewRegisterForm.getContent(), reviewRegisterForm.getRating(), user, house);
	
	model.addAttribute("successMessage", "レヴューを投稿しました。");
	
	return "redirect:/houses" + houseId;
}

@GetMapping("/{houseId}/reviews/{reviewId}/edit")
public String edit(@PathVariable(name = "houseId") Integer houseId, @PathVariable(name = "reviewId") Integer reviewId, Model model, Principal principal) {
	
	User user = userService.findByName(principal.getName());
	House house = houseService.findHouseById(houseId);
	Review review = reviewService.findReviewById(reviewId);
	
	if (house == null || !reviewService.existsReviewsForHouse(house)) {
        model.addAttribute("errorMessage", "指定されたページが見つかりません。");
        return "redirect:/houses/index";
	}
	
	if (!review.getHouse().getId().equals(houseId) || !review.getUser().getId().equals(user.getId())) {
		model.addAttribute("errorMessage", "不正なアクセスです。");
	}
	
	return "reviews/edit";
}

@PostMapping("/{houseId}/reviews/{reviewId}/update")
public String update(@PathVariable(name = "houseId") Integer houseId, @PathVariable(name = "reviewId") Integer reviewId, @Valid ReviewEditForm reviewEditForm,BindingResult bindingResult, Model model, Principal principal) {
	User user = userService.findByName(principal.getName());
	House house = houseService.findHouseById(houseId);
	Review review = reviewService.findReviewById(reviewId);
	
	if (bindingResult.hasErrors()) {
		return "reviews/edit";
	}
	
	if (house == null || !reviewService.existsReviewsForHouse(house)) {
        model.addAttribute("errorMessage", "指定されたページが見つかりません。");
        return "redirect:/houses/index";
	}
	
	if (!review.getHouse().getId().equals(houseId) || !review.getUser().getId().equals(user.getId())) {
		model.addAttribute("errorMessage", "不正なアクセスです。");
	}
	
	reviewService.updateReview(reviewId, reviewEditForm.getContent(), reviewEditForm.getRating());
	
	return "redirect:/houses" + houseId;
}
}
