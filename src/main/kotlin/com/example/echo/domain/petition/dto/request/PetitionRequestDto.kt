package com.example.echo.domain.petition.dto.request

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class PetitionRequestDto(
    @Schema(description = "회원의 ID", example = "1")
    val memberId: Long,

    @Schema(description = "청원 제목", example = "독도의 날(10월 25일), 국가기념일 지정에 관한 청원")
    val title: String,

    @Schema(description = "청원 내용", example = "1. 독도가 위험해지고 있습니다...")
    val content: String,

    @Schema(description = "청원 요약", example = "독도 보호에 대한 간략한 요약")
    val summary: String,

    @Schema(description = "청원 시작일", example = "2024-10-01T00:00:00")
    val startDate: LocalDateTime,

    @Schema(description = "청원 종료일", example = "2024-10-31T23:59:59")
    val endDate: LocalDateTime,

    @Schema(description = "청원 카테고리", example = "DIPLOMACY")
    val category: Category,

    @Schema(description = "원본 URL", example = "https://petitions.assembly.go.kr/proceed/onGoingAll/20D5FC4DDB8625D7E064B49691C6967B")
    val originalUrl: String,

    @Schema(description = "관련 뉴스", example = "https://www.newspenguin.com/news/articleView.html?idxno=16159")
    val relatedNews: String
) {
    fun toEntity(member: Member): Petition {
        return Petition(
            member = member,
            title = this.title,
            content = this.content,
            summary = this.summary,
            startDate = this.startDate,
            endDate = this.endDate,
            category = this.category,
            originalUrl = this.originalUrl,
            relatedNews = this.relatedNews
        )
    }

    fun toEntityWithExistingData(existingPetition: Petition, member: Member): Petition {
        return Petition(
            member = member,
            title = this.title,
            content = this.content,
            summary = this.summary,
            startDate = this.startDate,
            endDate = this.endDate,
            category = this.category,
            originalUrl = this.originalUrl,
            relatedNews = this.relatedNews,
            likesCount = existingPetition.likesCount,
            interestCount = existingPetition.interestCount,
            agreeCount = existingPetition.agreeCount
        )
    }
}