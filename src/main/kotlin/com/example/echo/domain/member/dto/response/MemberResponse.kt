package com.example.echo.domain.member.dto.response

import com.example.echo.domain.member.entity.Role
import io.swagger.v3.oas.annotations.media.Schema

data class MemberResponse (

    @field:Schema(description = "회원 ID", example = "1")
    val memberId: Long? = null,

    @field:Schema(description = "사용자 ID", example = "user1")
    val userId: String = "",

    @field:Schema(description = "회원 이름", example = "홍길동")
    val name: String = "",

    @field:Schema(description = "회원 이메일", example = "user1@example.com")
    val email: String = "",

    @field:Schema(description = "회원 전화번호", example = "010-1234-5678")
    val phone: String = "",

    @field:Schema(description = "회원 프로필 이미지 URL", example = "/images/default-avatar.png")
    val avatarImage: String? = null,

    @field:Schema(description = "회원 역할", example = "USER")
    val role: Role = Role.USER
)
