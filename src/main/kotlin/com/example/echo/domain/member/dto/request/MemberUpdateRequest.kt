package com.example.echo.domain.member.dto.request

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class MemberUpdateRequest(
    @Schema(description = "사용자 ID", example = "user123")
    val userId: String? = null,

    @Schema(description = "사용자 이름", example = "홍길동")
    val name: String? = null,

    @Schema(description = "이메일 주소", example = "example@example.com", required = true)
    @field:Email(message = "유효한 이메일 주소를 입력하세요.")
    @field:NotBlank(message = "이메일은 필수 항목입니다.")
    val email: String,

    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    @field:NotBlank(message = "전화번호는 필수 항목입니다.")
    val phone: String,

    @Schema(description = "아바타 이미지 URL", example = "/images/user-avatar.png")
    val avatarImage: String? = null,

    @Schema(description = "사용자 권한", example = "USER")
    val role: Role? = null
) {
    fun updateMember(member: Member) {
        userId?.let { member.userId = it }
        name?.let { member.name = it }
        member.email = this.email
        member.phone = this.phone
        member.avatarImage = this.avatarImage ?: "/images/avatar-default.png"
        role?.let { member.role = it }
    }
}
