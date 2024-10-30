package com.example.echo.domain.member.dto.request

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.entity.Role
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

data class MemberUpdateRequest(

    @field:Schema(description = "사용자 ID", example = "user123")
    val userId: String? = null,

    @field:Schema(description = "사용자 이름", example = "홍길동")
    val name: String? = null,

    @field:Schema(description = "이메일 주소", example = "example@example.com")
    @field:Email(message = "유효한 이메일 주소를 입력하세요.")
    val email: String? = null,

    @field:Schema(description = "전화번호", example = "010-1234-5678")
    val phone: String? = null,

    @field:Schema(description = "아바타 이미지 URL", example = "/images/user-avatar.png")
    val avatarImage: String? = null,

    @field:Schema(description = "사용자 권한", example = "USER")
    val role: Role? = null,

    @field:Schema(description = "현재 비밀번호 (비밀번호 변경 시 필수)", example = "CurrentPass123!")
    val currentPassword: String? = null,

    @field:Schema(description = "새로운 비밀번호", example = "NewPass123!")
    val newPassword: String? = null
) {
    // 유효성 검사: 새 비밀번호를 입력할 경우 현재 비밀번호도 필수
    fun validatePasswordUpdate() {
        if (newPassword != null && currentPassword == null) {
            throw IllegalArgumentException("새로운 비밀번호를 설정하려면 현재 비밀번호를 입력해야 합니다.")
        }
    }

    // 각각 null이 아닐 때만 member의 해당 속성을 업데이트
    fun updateMember(member: Member) {
        // 비밀번호 변경 요청이 있을 경우 유효성 검사 수행
        if (newPassword != null) {
            validatePasswordUpdate()
        }

        userId?.let { member.userId = it }
        name?.let { member.name = it }
        email?.let { member.email = it }
        phone?.let { member.phone = it }
        avatarImage?.let { member.avatarImage = it }
        role?.let { member.role = it }
    }
}
