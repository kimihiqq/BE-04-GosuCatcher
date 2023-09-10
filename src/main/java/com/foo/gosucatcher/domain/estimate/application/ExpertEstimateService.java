package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpertEstimateService {

	private final ExpertEstimateRepository expertResponseRepository;
	private final MemberEstimateRepository memberEstimateRepository;
	private final ExpertRepository expertRepository;
	private final SubItemRepository subItemRepository;

	public ExpertEstimateResponse create(Long expertId, ExpertEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		MemberEstimate memberEstimate = memberEstimateRepository.findById(request.memberEstimateId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		SubItem subItem = subItemRepository.findById(request.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		ExpertEstimate expertEstimate = ExpertEstimateCreateRequest.toExpertResponseEstimate(request, memberEstimate,
			expert, subItem);

		expertResponseRepository.save(expertEstimate);

		return ExpertEstimateResponse.from(expertEstimate);
	}

	@Transactional(readOnly = true)
	public ExpertEstimatesResponse findAll() {
		List<ExpertEstimate> expertEstimateList = expertResponseRepository.findAll();

		return ExpertEstimatesResponse.from(expertEstimateList);
	}

	@Transactional(readOnly = true)
	public ExpertEstimateResponse findById(Long id) {
		ExpertEstimate expertEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_ESTIMATE));

		return ExpertEstimateResponse.from(expertEstimate);
	}

	public Long update(Long id, ExpertEstimateUpdateRequest request) {
		ExpertEstimate foundExpertEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_ESTIMATE));

		ExpertEstimate expertEstimate = ExpertEstimateUpdateRequest.toExpertResponseEstimate(request,
			foundExpertEstimate.getExpert(), foundExpertEstimate.getMemberEstimate());

		foundExpertEstimate.update(expertEstimate);

		return foundExpertEstimate.getId();
	}

	public void delete(Long id) {
		ExpertEstimate expertEstimate = expertResponseRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_ESTIMATE));

		expertResponseRepository.delete(expertEstimate);
	}
}
