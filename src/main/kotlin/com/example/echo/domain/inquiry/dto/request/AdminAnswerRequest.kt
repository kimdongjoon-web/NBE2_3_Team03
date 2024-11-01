package com.example.echo.domain.inquiry.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class AdminAnswerRequest (

    @field:NotBlank(message = "답변 내용을 입력해주세요.")
    @Schema(description = "관리자가 입력할 답변 내용", example = "1:1 문의에 대한 답변입니다.")
    var replyContent: String? = null
)