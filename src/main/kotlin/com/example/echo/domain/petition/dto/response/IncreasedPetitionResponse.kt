package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.LocalDateTime

class IncreasedPetitionResponse(petition: Petition) : Serializable {
    @Schema(description = "청원의 ID", example = "1")
    var petitionId: Long? = null

    @Schema(description = "청원 제목", example = "독도의 날(10월 25일), 국가기념일 지정에 관한 청원")
    var title: String? = null

    @Schema(description = "청원 시작일", example = "2024-10-01T12:00:00")
    var startDate: LocalDateTime? = null

    @Schema(description = "청원 종료일", example = "2024-10-31T12:00:00")
    var endDate: LocalDateTime? = null

    @Schema(description = "청원 카테고리", example = "DIPLOMACY")
    var category: Category? = null

    @Schema(description = "좋아요 수", example = "150")
    var likesCount: Int? = null

    @Schema(description = "관심 수", example = "200")
    var interestCount: Int? = null

    @Schema(description = "동의 수", example = "20527")
    var agreeCount: Int? = null

    @Schema(description = "이전 동의 수", example = "20000") // 추가된 필드
    var previousAgreeCount: Int = 0 // 기본값 0

    init {
        this.petitionId = petition.petitionId
        this.title = petition.title
        this.startDate = petition.startDate
        this.endDate = petition.endDate
        this.category = petition.category
        this.likesCount = petition.likesCount
        this.interestCount = petition.interestCount
        this.agreeCount = petition.agreeCount
        this.previousAgreeCount = petition.previousAgreeCount // 이전 동의자 수 추가
    }

    companion object {
        private const val serialVersionUID = 1L // serialVersionUID 추가
    }
}