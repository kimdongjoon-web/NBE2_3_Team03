package com.example.echo.domain.member.dto.request

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class MemberCreateRequest(

    @field:Schema(description = "사용자 ID", example = "user123")
    @field:NotBlank(message = "아이디는 필수 항목입니다.")
    val userId: String,

    @field:Schema(description = "사용자 이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수 항목입니다.")
    val name: String,

    @field:Schema(description = "사용자 이메일", example = "example@example.com")
    @field:Email(message = "유효한 이메일 주소를 입력하세요.")
    @field:NotBlank(message = "이메일은 필수 항목입니다.")
    val email: String,

    @field:Schema(description = "사용자 비밀번호", example = "1111")
    @field:NotBlank(message = "비밀번호는 필수 항목입니다.")
    val password: String,

    @field:Schema(description = "전화번호", example = "010-1234-5678")
    @field:NotBlank(message = "전화번호는 필수 항목입니다.")
    val phone: String,

    @field:Schema(description = "사용자 아바타 이미지 URL", example = "/images/user-avatar.png")
    val avatarImage: String? = null,

    @field:Schema(description = "사용자 역할", example = "USER")
    val role: Role
) {
    fun toMember(): Member {
        return Member(
            userId = this.userId,
            name = this.name,
            email = this.email,
            password = this.password,
            phone = this.phone,
            avatarImage = this.avatarImage ?: "/images/default-avatar.png", // 기본 아바타 이미지 설정
            role = this.role
        )
    }
}
