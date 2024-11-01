package com.example.echo.domain.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class MemberLoginRequest(

    @field:Schema(description = "사용자 ID", example = "user123")
    @field:NotBlank(message = "아이디는 필수 항목입니다.")
    val userId: String,

    @field:Schema(description = "사용자 비밀번호", example = "1111")
    @field:NotBlank(message = "비밀번호는 필수 항목입니다.")
    val password: String
)
