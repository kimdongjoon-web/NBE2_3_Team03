package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class PetitionDetailResponseDto (petition: Petition){
    @Schema(description = "청원의 ID", example = "1")
    var petitionId: Long? = null

    @Schema(description = "청원을 생성한 회원의 ID", example = "1")
    var memberId: Long? = null

    @Schema(description = "청원 제목", example = "독도의 날(10월 25일), 국가기념일 지정에 관한 청원")
    var title: String? = null

    @Schema(description = "청원 내용", example = "1. 독도가 위험해지고 있습니다...")
    var content: String? = null

    @Schema(description = "청원 요약", example = "독도 보호에 대한 간략한 요약")
    var summary: String? = null

    @Schema(description = "청원 시작일", example = "2024-10-01T12:00:00")
    var startDate: LocalDateTime? = null

    @Schema(description = "청원 종료일", example = "2024-10-31T12:00:00")
    var endDate: LocalDateTime? = null

    @Schema(description = "청원 카테고리", example = "DIPLOMACY")
    var category: Category? = null

    @Schema(
        description = "원본 URL",
        example = "https://petitions.assembly.go.kr/proceed/onGoingAll/20D5FC4DDB8625D7E064B49691C6967B"
    )
    var originalUrl: String? = null

    @Schema(description = "관련 뉴스", example = "https://www.newspenguin.com/news/articleView.html?idxno=16159")
    var relatedNews: String? = null

    @Schema(description = "좋아요 수", example = "150")
    var likesCount: Int? = null

    @Schema(description = "관심 수", example = "200")
    var interestCount: Int? = null

    @Schema(description = "동의 수", example = "20527")
     var agreeCount: Int? = null

    init {
        this.petitionId = petition.petitionId
        this.memberId = petition.member?.memberId // Null 체크
        this.title = petition.title
        this.summary = petition.summary
        this.content = petition.content
        this.startDate = petition.startDate
        this.endDate = petition.endDate
        this.category = petition.category
        this.originalUrl = petition.originalUrl
        this.relatedNews = petition.relatedNews
        this.likesCount = petition.likesCount
        this.interestCount = petition.interestCount
        this.agreeCount = petition.agreeCount
    }


}