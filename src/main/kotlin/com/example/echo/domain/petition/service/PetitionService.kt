package com.example.echo.domain.petition.service

import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.dto.request.PetitionRequestDto
import com.example.echo.domain.petition.dto.response.PetitionDetailResponseDto
import com.example.echo.domain.petition.dto.response.PetitionResponseDto
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class PetitionService (
    private val petitionRepository: PetitionRepository,
    private val memberRepository: MemberRepository,
    private val SummarizationService: SummarizationService
){
    // 청원 등록
    @Transactional
    fun createPetition(petitionDto: PetitionRequestDto): PetitionDetailResponseDto {
        // 청원 등록을 위한 관리자 아이디 검색
        val member = memberRepository.findById(petitionDto.memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }

        val petition = petitionDto.toEntity(member)
        return PetitionDetailResponseDto(petitionRepository.save(petition))
    }

    // 청원 단건 조회
    @Transactional // 요약 저장하는 경우 있음
    fun getPetitionById(petitionId: Long): PetitionDetailResponseDto {
        val petition = petitionRepository.findById(petitionId).orElseThrow {
            PetitionCustomException(
                ErrorCode.PETITION_NOT_FOUND
            )
        } // 청원 번호 조회 없으면 예외
        // 청원 기간 만료 체크 -> 따로 서비스 층에 작성
        //  위에서 exception 발생 안함 = 청원이 존재한다는 뜻 -> 단순 날짜 비교만 진행
        if (isExpired(petition)) { // 만료되었으면 예외 발생
            throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)
        }

        val summary = petition.summary // 요약 내용 체크

        if (summary != null && !summary.isEmpty()) {
            // 내용 요약 있으면 바로 반환
            return PetitionDetailResponseDto(petition)
        } else {
            // 내용 요약 없으면 요약 진행 및 저장 후 반환
            val content = petition.content // 원본 내용
            // 줄바꿈 문단 처리 방식 -> 공백 전부 제거 or 줄바꿈은 유지
            val originText = content!!.replace("\\s+".toRegex(), " ")
            val summaryText: String = SummarizationService.getSummarizedText(originText)// 요약 결과
            // null -> 요약된 내용으로 변경
            petition.changeSummary(summaryText)
            petitionRepository.save(petition)
            return PetitionDetailResponseDto(petition)
        }
    }

    // 청원 전체 조회
    fun getPetitions(pageable: Pageable): Page<PetitionResponseDto> {
        return petitionRepository.findAll(pageable).map { PetitionResponseDto(it) }
    }


    // 진행 중인 청원 전체 조회
    fun getOngoingPetitions(pageable: Pageable): Page<PetitionResponseDto> {
        return petitionRepository.findAllOngoing(pageable).map { PetitionResponseDto(it)}
    }

    // 청원 전체 조회 (카테고리별)
    fun getPetitionsByCategory(pageable: Pageable, category: Category): Page<PetitionResponseDto> {
        return petitionRepository.findByCategory(pageable, category)
            .map { PetitionResponseDto(it) }
    }

    val endDatePetitions: List<PetitionResponseDto>
        // 청원 만료일 순 5개 조회
        get() {
            val pageable: Pageable = PageRequest.of(0, 5)
            return petitionRepository.getEndDatePetitions(pageable)
        }

    val likesCountPetitions: List<PetitionResponseDto>
        // 청원 동의자 순 5개 조회
        get() {
            val pageable: Pageable = PageRequest.of(0, 5)
            return petitionRepository.getLikesCountPetitions(pageable)
        }

    // 좋아요 기능
    @Transactional
    fun toggleLikeOnPetition(petitionId: Long, memberId: Long): String {
        if (memberId == null) {
            throw PetitionCustomException(ErrorCode.USER_NOT_MEMBER)
        }

        // 멤버 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) // 여기에서 발생시키는 예외
        }

        // 청원 조회
        val petition = petitionRepository.findById(petitionId)
            .orElseThrow { PetitionCustomException(ErrorCode.PETITION_NOT_FOUND) }

        // 좋아요를 추가하거나 제거
        val isLiked = petition.toggleLike(memberId)

        // 변경 사항을 저장
        petitionRepository.save(petition)

        // 좋아요가 추가되었는지 제거되었는지에 따라 적절한 메시지 반환
        val message = if (isLiked) "좋아요가 추가되었습니다." else "좋아요가 제거되었습니다."
        return message
    }

    // 청원 카테고리 선택 5개 조회 (랜덤 순)
    fun getRandomCategoryPetitions(category: Category): List<PetitionResponseDto> {
        val pageable: Pageable = PageRequest.of(0, 5)
        return petitionRepository.getCategoryPetitionsInRandomOrder(category, pageable)
    }

    // 청원 수정
    @Transactional
    fun updatePetition(petitionId: Long, updatedPetitionDto: PetitionRequestDto): PetitionDetailResponseDto {
        val existingPetition = petitionRepository.findById(petitionId)
            .orElseThrow { PetitionCustomException(ErrorCode.PETITION_NOT_FOUND) }

        val member = memberRepository.findById(updatedPetitionDto.memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }

        val updatedPetition = updatedPetitionDto.toEntityWithExistingData(existingPetition, member)
        return PetitionDetailResponseDto(petitionRepository.save(updatedPetition))
    }

    // 청원 삭제
    @Transactional
    fun deletePetitionById(petitionId: Long) {
        if (!petitionRepository.existsById(petitionId)) {
            throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)
        }
        petitionRepository.deleteById(petitionId)
    }

    // 청원 기간 체크
    fun isExpired(petition: Petition): Boolean { // petition or petitionId
        // 오늘 날짜와 비교
        // 만료 전이면 false
        requireNotNull(petition.endDate) { "만료일은 null 일 수 없습니다." }
        // 현재 형식이 LocalDateTime 만료일 00:00:00
        // 청원 만료일 오후 3시 일 경우 만료 전이나 이미 만료됐다고 판단
        // 만료일 + 1 을 기준으로 체크
        return petition.endDate!!.plusDays(1).isBefore(LocalDateTime.now()) // 만료일이 지난 경우 true
    }

    // 제목으로 청원 검색
    fun searchPetitionsByTitle(query: String?): List<PetitionDetailResponseDto> {
        val petitions = petitionRepository.findByTitleContainingIgnoreCase(query!!)
        return petitions.stream()
            .map { petition: Petition? -> PetitionDetailResponseDto(petition!!) }
            .collect(Collectors.toList())
    }

}