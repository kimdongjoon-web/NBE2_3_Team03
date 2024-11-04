package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.Petition
import io.swagger.v3.oas.annotations.media.Schema

data class InterestPetitionResponseDTO(
    @field:Schema(description = "청원의 ID", example = "1")
    val petitionId: Long? = null,

    @field:Schema(description = "청원 제목", example = "독도의 날(10월 25일), 국가기념일 지정에 관한 청원")
    val title: String? = null,

    @field:Schema(description = "청원 내용", example = "1. 독도가 위험해지고 있습니다...")
    val content: String? = null,

    @field:Schema(description = "관심 수", example = "150")
    val interestCount: Int? = null
) {
    constructor(petition: Petition) : this(
        petitionId = petition.petitionId,
        title = petition.title,
        content = petition.content,
        interestCount = petition.interestCount
    )
}
