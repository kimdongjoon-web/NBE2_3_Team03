package com.example.echo.domain.petition.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class InterestRequestDTO(
    @field:Schema(description = "청원의 ID", example = "1")
    val petitionId: Long,

    @field:Schema(description = "회원의 ID", example = "1")
    val memberId: Long
)
