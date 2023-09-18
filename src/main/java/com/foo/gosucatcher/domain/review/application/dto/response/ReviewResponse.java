package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foo.gosucatcher.domain.review.domain.Review;
import com.foo.gosucatcher.domain.review.domain.ReviewImage;

public record ReviewResponse(
	Long id,
	Long expertId,
	Long writerId,
	Long subItemId,
	String content,
	double rating,
	boolean replyExisted,
	Map<String, String> reply,

	List<String> images,

	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	// public static ReviewResponse from(Review review) {
	// 	Map<String, String> reply = new HashMap<>();
	// 	boolean replyExisted = review.getReply() != null;
	//
	// 	if (replyExisted) {
	// 		reply.put("id", review.getReply().getId().toString());
	// 		reply.put("content", review.getReply().getContent());
	// 		reply.put("createdAt", review.getReply().getCreatedAt().toString());
	// 		reply.put("UpdatedAt", review.getReply().getUpdatedAt().toString());
	// 	}
	//
	// 	return new ReviewResponse(review.getId(), review.getExpert().getId(), review.getWriter().getId(),
	// 		review.getSubItem().getId(), review.getContent(), review.getRating(), replyExisted,
	// 		reply, List.of(), review.getCreatedAt(), review.getUpdatedAt());
	// }

	public static ReviewResponse from(Review review) {
		Map<String, String> reply = new HashMap<>();
		boolean replyExisted = review.getReply() != null;

		if (replyExisted) {
			reply.put("id", review.getReply().getId().toString());
			reply.put("content", review.getReply().getContent());
			reply.put("createdAt", review.getReply().getCreatedAt().toString());
			reply.put("UpdatedAt", review.getReply().getUpdatedAt().toString());
		}

		List<String> reviewImages = List.of();
		if (review.getReviewImages() != null) {
			reviewImages = review.getReviewImages().stream().map(ReviewImage::getPath).toList();
		}

		return new ReviewResponse(review.getId(), review.getExpert().getId(), review.getWriter().getId(),
			review.getSubItem().getId(), review.getContent(), review.getRating(), replyExisted,
			reply, reviewImages, review.getCreatedAt(),
			review.getUpdatedAt());
	}
}
