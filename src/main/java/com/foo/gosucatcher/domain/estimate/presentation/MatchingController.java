package com.foo.gosucatcher.domain.estimate.presentation;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.foo.gosucatcher.domain.estimate.application.MatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;
	private final MemberEstimateService memberEstimateService;
	private final ChattingRoomService chattingRoomService;

	@PostMapping("/auto/{memberId}")
	public ResponseEntity<ChattingRoomsResponse> createAutoEstimate(@PathVariable Long memberId,
																	@Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.create(memberId, memberEstimateRequest);

		//매칭된 바로 견적 리스트
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = matchingService.match(memberEstimateResponse.subItemId(), memberEstimateResponse.location());

		//요청 견적서에 매칭된 바로 견적들 삽입 (update)
		Long memberEstimateId = memberEstimateService.updateExpertEstimates(memberEstimateResponse.id(), expertAutoEstimatesResponse.expertAutoEstimateResponses());

		//채팅방 생성
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);

		return ResponseEntity.ok(chattingRoomsResponse);
	}
}
