package com.example.echo.domain.member.dto.response

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import io.swagger.v3.oas.annotations.media.Schema

data class MemberResponse(
    @Schema(description = "회원 ID", example = "1")
    val memberId: Long,

    @Schema(description = "사용자 ID", example = "user1")
    val userId: String,

    @Schema(description = "회원 이름", example = "홍길동")
    val name: String,

    @Schema(description = "회원 이메일", example = "user1@example.com")
    val email: String,

    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    val phone: String,

    @Schema(description = "회원 프로필 이미지 URL", example = "/images/default-avatar.png")
    val avatarImage: String? = null,

    @Schema(description = "회원 역할", example = "USER")
    val role: Role
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                memberId = member.memberId!!,
                userId = member.userId,
                name = member.name,
                email = member.email,
                phone = member.phone,
                avatarImage = member.avatarImage,
                role = member.role
            )
        }
    }
}
