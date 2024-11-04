package com.example.echo.domain.petition.controller

import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.dto.request.InterestRequestDTO
import com.example.echo.domain.petition.dto.request.PetitionRequestDto
import com.example.echo.domain.petition.dto.response.AgeGroupInterestCountResponse
import com.example.echo.domain.petition.dto.response.IncreasedPetitionResponse
import com.example.echo.domain.petition.dto.response.InterestPetitionResponseDTO
import com.example.echo.domain.petition.dto.response.PetitionDetailResponseDto
import com.example.echo.domain.petition.dto.response.PetitionResponseDto
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.service.AgeGroupInterestCountService
import com.example.echo.domain.petition.service.AgreeCountMonitoringService
import com.example.echo.domain.petition.service.PetitionService
import com.example.echo.global.api.ApiResponse
import com.example.echo.global.security.auth.CustomUserPrincipal
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/petitions")
@Tag(name = "Petition Controller", description = "청원 관리 API")
class PetitionController (
    private val petitionService: PetitionService,
    private val memberRepository: MemberRepository,
    private val agreeCountMonitoringService: AgreeCountMonitoringService,
    private val ageGroupInterestCountService: AgeGroupInterestCountService
) {
    // 청원 등록
    @Operation(summary = "청원 등록", description = "새로운 청원을 등록합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createPetition(
        @Parameter(description = "청원 등록 요청 정보", required = true) @RequestBody petitionDto: PetitionRequestDto
    ): ResponseEntity<ApiResponse<PetitionDetailResponseDto>> {
        val createdPetition = petitionService.createPetition(petitionDto)
        return ResponseEntity.ok(ApiResponse.success(createdPetition))
    }

    // 청원 단건 조회
    @Operation(summary = "청원 단건 조회", description = "특정 ID의 청원을 조회합니다.")
    @GetMapping("/{petitionId}")
    fun getPetitionById(
        @Parameter(description = "조회할 청원의 ID", required = true) @PathVariable petitionId: Long
    ): ResponseEntity<ApiResponse<PetitionDetailResponseDto>> {
        val petition = petitionService.getPetitionById(petitionId)
        return ResponseEntity.ok(ApiResponse.success(petition))
    }

    // 청원 전체 조회
    @Operation(summary = "청원 전체 조회", description = "모든 청원을 페이지별로 조회합니다.")
    @GetMapping
    fun getPetitions(
        @Parameter(description = "청원 조회 페이징 요청 정보", required = true) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PetitionResponseDto>>> {
        val petitions = petitionService.getOngoingPetitions(pageable)
        return ResponseEntity.ok(ApiResponse.success(petitions))
    }

    // 청원 카테고리별 조회
    @Operation(summary = "카테고리별 청원 조회", description = "특정 카테고리의 모든 청원을 페이지별로 조회합니다.")
    @GetMapping("/category/{category}")
    fun getPetitionsByCategory(
        @Parameter(description = "조회할 청원의 카테고리", required = true) @PathVariable category: Category,
        @Parameter(description = "청원 조회 페이징 요청 정보", required = true) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PetitionResponseDto>>> {
        val petitions = petitionService.getPetitionsByCategory(pageable, category)
        return ResponseEntity.ok(ApiResponse.success(petitions))
    }

    @GetMapping("/view/endDate")
    @Operation(summary = "청원 만료일 기준 조회", description = "만료일이 가까운 청원 5개를 조회합니다.")
    fun getEndDatePetitions(): ResponseEntity<ApiResponse<List<PetitionResponseDto>>> {
        val endDatePetitions = petitionService.endDatePetitions
        return ResponseEntity.ok(ApiResponse.success(endDatePetitions))
    }

    @GetMapping("/view/likesCount")
    @Operation(summary = "청원 좋아요 수 기준 조회", description = "좋아요 수가 많은 청원 5개를 조회합니다.")
    fun getLikesCountPetitions(): ResponseEntity<ApiResponse<List<PetitionResponseDto>>> {
        val likesCountPetitions = petitionService.getLikesCountPetitions()
        return ResponseEntity.ok(ApiResponse.success(likesCountPetitions))
    }

    // 청원 좋아요 기능
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @Operation(summary = "청원 좋아요 토글", description = "청원에 좋아요를 추가하거나 제거합니다.")
    @PostMapping("/{petitionId}/like")
    fun toggleLike(
        @Parameter(description = "좋아요를 추가하거나 제거할 청원의 ID", required = true) @PathVariable petitionId: Long,
        @Parameter(description = "좋아요를 클릭한 회원의 ID", required = true) @RequestParam(required = false) memberId: Long
    ): ResponseEntity<ApiResponse<String>> {
        val message = petitionService.toggleLikeOnPetition(petitionId, memberId)
        return ResponseEntity.ok(ApiResponse.success(message))
    }

    // 청원 카테고리 선택 5개 조회
    @Operation(summary = "청원 카테고리별 조회", description = "특정 카테고리의 청원 5개를 랜덤으로 조회합니다.")
    @GetMapping("/view/category/{category}")
    fun getRandomCategoryPetitions(
        @Parameter(description = "랜덤으로 조회할 청원의 카테고리", required = true) @PathVariable category: Category
    ): ResponseEntity<ApiResponse<List<PetitionResponseDto>>> {
        val categoryPetitions = petitionService.getRandomCategoryPetitions(category)
        return ResponseEntity.ok(ApiResponse.success(categoryPetitions))
    }

    // 제목으로 청원 검색
    @Operation(summary = "청원 제목으로 검색", description = "제목에 검색어가 포함된 청원을 조회합니다.")
    @GetMapping("/search")
    fun searchPetitions(
        @Parameter(description = "검색할 제목의 키워드", required = true) @RequestParam query: String?
    ): ResponseEntity<ApiResponse<List<PetitionDetailResponseDto>>> {
        val petitions: List<PetitionDetailResponseDto> = petitionService.searchPetitionsByTitle(query)
        return ResponseEntity.ok(ApiResponse.success(petitions))
    }

    // 청원 수정
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "청원 수정", description = "특정 ID의 청원을 수정합니다.")
    @PutMapping("/{petitionId}")
    fun updatePetition(
        @Parameter(description = "수정할 청원의 ID", required = true) @PathVariable petitionId: Long,
        @Parameter(description = "청원 수정 요청 정보", required = true) @RequestBody petitionDto: PetitionRequestDto
    ): ResponseEntity<ApiResponse<PetitionDetailResponseDto>> {
        val updatedPetition = petitionService.updatePetition(petitionId, petitionDto)
        return ResponseEntity.ok(ApiResponse.success(updatedPetition))
    }

    // 청원 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "청원 삭제", description = "특정 ID의 청원을 삭제합니다.")
    @DeleteMapping("/{petitionId}")
    fun deletePetitionById(
        @Parameter(description = "삭제할 청원의 ID", required = true) @PathVariable petitionId: Long
    ): ResponseEntity<ApiResponse<Void>> {
        petitionService.deletePetitionById(petitionId)
        return ResponseEntity.noContent().build()
    }

    // 관심 목록 추가
    @PreAuthorize("authentication.principal.memberId == #requestDTO.memberId")
    @Operation(summary = "관심 목록 추가", description = "청원을 관심 목록에 추가합니다.")
    @PostMapping("/interestAdd")
    fun addInterest(
        @Parameter(description = "관심 목록 추가 요청 정보", required = true) @RequestBody requestDTO: InterestRequestDTO
    ): ResponseEntity<ApiResponse<Any?>> {
        return try {
            petitionService.addInterest(requestDTO)
            ResponseEntity.ok(ApiResponse.success("추가되었습니다.", null))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Entity not found"))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(ApiResponse.error("관심사 추가 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    // 관심 목록 제거
    @PreAuthorize("authentication.principal.memberId == #requestDTO.memberId")
    @Operation(summary = "관심 목록 제거", description = "청원을 관심 목록에서 제거합니다.")
    @PostMapping("/interestRemove")
    fun removeInterest(
        @Parameter(description = "관심 목록 제거 요청 정보", required = true) @RequestBody requestDTO: InterestRequestDTO
    ): ResponseEntity<ApiResponse<Any?>> {
        return try {
            petitionService.removeInterest(requestDTO)
            ResponseEntity.ok(ApiResponse.success("관심사가 성공적으로 제거되었습니다.", null))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Entity not found"))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(ApiResponse.error("관심사 제거 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    // 나의 관심 목록 조회
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 관심 목록 조회", description = "현재 사용자의 관심 목록을 조회합니다.")
    @GetMapping("/Myinterest")
    fun getInterestList(
        @Parameter(
            description = "현재 인증된 사용자 정보", required = true
        )
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<ApiResponse<*>> {
        val member = memberRepository.findById(principal.memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }

        return try {
            // 회원의 관심 목록 조회
            val interestList: List<InterestPetitionResponseDTO> = petitionService.getInterestList(member)
            ResponseEntity.ok(ApiResponse.success(interestList))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(error("관심 목록 조회 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    // 관심 목록 수에 따라 정렬
    @Operation(summary = "관심 목록 수 기준 조회", description = "관심 목록 수에 따라 청원을 정렬하여 조회합니다.")
    @GetMapping("/interests")
    fun getPetitionsByInterestCount(): ResponseEntity<ApiResponse<List<InterestPetitionResponseDTO>>> {
        return try {
            val petitionList: List<InterestPetitionResponseDTO> = petitionService.getPetitionsByInterestCount()
            ResponseEntity.ok(ApiResponse.success(petitionList))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(ApiResponse.error("관심사 순위 목록 조회 중 오류가 발생했습니다: ${e.message}"))
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "나이 기준 관심 청원 조회", description = "사용자의 나이와 비슷한 이용자의 관심 청원을 조회합니다.")
    @GetMapping("/age")
    fun getPetitionsByAge(
        @Parameter(
            description = "현재 인증된 사용자 정보", required = true)
        @AuthenticationPrincipal principal: CustomUserPrincipal
    ): ResponseEntity<ApiResponse<List<AgeGroupInterestCountResponse>>> {
        val member = memberRepository.findById(principal.memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }

        return try {
            // 회원의 관심 목록 조회
            val interestPetitions = ageGroupInterestCountService.getTopPetitionsByAgeGroup(member)
            ResponseEntity.ok(ApiResponse.success(interestPetitions))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(error("나이 기준 조회 중 오류가 발생했습니다: ${e.message}"))
        }
    }


    // 동의자 수 업데이트 후, 급증 청원 리스트 요청
    @Operation(summary = "동의자 수 급증 청원 데이터 조회", description = "동의자 수가 크게 증가한 순으로 청원을 정렬하여 조회합니다.")
    @GetMapping("/increased")
    fun getIncreasedPetitions(): List<IncreasedPetitionResponse> = agreeCountMonitoringService.increasedAgreeCountList()
}