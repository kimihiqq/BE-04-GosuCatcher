package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;

public record ExpertEstimateResponse(
	Long id,
	ExpertResponse expert,
	MemberRequestEstimateResponse memberRequestEstimate,
	int totalCost,
	String activityLocation,
	String description
) {

	public static ExpertEstimateResponse from(ExpertEstimate expertEstimate) {
		MemberRequestEstimate memberRequestEstimate = expertEstimate.getMemberRequestEstimate() != null ?
			expertEstimate.getMemberRequestEstimate() : null;

		return new ExpertEstimateResponse(
			expertEstimate.getId(),
			ExpertResponse.from(expertEstimate.getExpert()),
			MemberRequestEstimateResponse.from(memberRequestEstimate),
			expertEstimate.getTotalCost(),
			expertEstimate.getActivityLocation(),
			expertEstimate.getDescription());
	}
}
