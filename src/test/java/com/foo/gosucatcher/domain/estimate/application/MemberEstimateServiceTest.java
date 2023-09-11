package com.foo.gosucatcher.domain.estimate.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberEstimateServiceTest {

	@Mock
	private MemberEstimateRepository memberEstimateRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private SubItemRepository subItemRepository;

	@InjectMocks
	private MemberEstimateService memberEstimateService;

	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private MemberEstimate memberEstimate;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();
	}

	@DisplayName("회원 요청 견적서 저장 성공 테스트")
	@Test
	void create() {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		Long memberEstimateId = 1L;

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId, memberEstimate.getLocation(),
			memberEstimate.getPreferredStartDate(), memberEstimate.getDetailedDescription());

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(subItemRepository.findById(subItemId)).thenReturn(Optional.of(subItem));
		when(memberEstimateRepository.save(any(MemberEstimate.class))).thenReturn(memberEstimate);
		when(memberEstimateRepository.findById(memberEstimateId)).thenReturn(Optional.of(memberEstimate));

		//when
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.create(memberId, memberEstimateRequest);
		MemberEstimate result = memberEstimateRepository.findById(memberEstimateId).get();

		//then
		assertThat(memberEstimateResponse.location()).isEqualTo(result.getLocation());
		assertThat(memberEstimateResponse.preferredStartDate()).isEqualTo(result.getPreferredStartDate());
		assertThat(memberEstimateResponse.detailedDescription()).isEqualTo(result.getDetailedDescription());
	}

	@DisplayName("회원 요청 견적서 생성 실패 테스트 - 희망 시작일이 현재보다 이전인 경우")
	@Test
	void createFailed() {
		//given
		//when
		//then
		assertThrows(BusinessException.class, () -> MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().minusDays(3))
			.detailedDescription("추가 내용")
			.build());
	}

	@DisplayName("회원 요청 견적서 회원별 전체 조회 테스트")
	@Test
	void findAllByMember() {
		//given
		Long memberId = 1L;

		MemberEstimate memberEstimate2 = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포2동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용2")
			.build();

		List<MemberEstimate> estimates = List.of(memberEstimate, memberEstimate2);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(memberEstimateRepository.findAllByMember(member)).thenReturn(estimates);

		//when
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllByMember(memberId);

		//then
		assertThat(memberEstimatesResponse.memberEstimates()).hasSize(2);
	}

	@DisplayName("회원 요청 견적서 회원 id로 조회 테스트")
	@Test
	void findById() {
		//given
		Long memberEstimateId = 1L;

		when(memberEstimateRepository.findById(memberEstimateId)).thenReturn(Optional.of(memberEstimate));

		//when
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.findById(memberEstimateId);

		//then
		assertThat(memberEstimateResponse.location()).isEqualTo(memberEstimate.getLocation());
		assertThat(memberEstimateResponse.preferredStartDate()).isEqualTo(memberEstimate.getPreferredStartDate());
		assertThat(memberEstimateResponse.detailedDescription()).isEqualTo(memberEstimate.getDetailedDescription());
	}
}
