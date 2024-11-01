package com.example.echo.domain.petition.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class PetitionCrawlResponse(

    @Schema(description = "청원 제목", example = "독도의 날(10월 25일), 국가기념일 지정에 관한 청원")
    var title: String? = null,

    @Schema(description = "동의 시작일과 종료일 (형식: yyyy-MM-dd)", example = "2024-09-11 ~ 2024-10-11")
    var period: String? = null,

    @Schema(description = "청원 분류", example = "외교/통일/국방/안보")
    var category: String? = null,

    @Schema(description = "동의자 수 (형식: 1,000명)", example = "20,527명")
    var agreeCount: String? = null,

    @Schema(
        description = "청원 원본 주소",
        example = "https://petitions.assembly.go.kr/proceed/onGoingAll/20D5FC4DDB8625D7E064B49691C6967B"
    )
    var href: String? = null,

    @Schema(description = "청원 상세 내용", example = "1. 독도가 위험해지고 있습니다...")
    var content: String? = null
)