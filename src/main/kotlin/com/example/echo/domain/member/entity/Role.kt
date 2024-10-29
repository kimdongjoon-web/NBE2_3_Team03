package com.example.echo.domain.member.entity

enum class Role (
    val description: String
) {
    ADMIN("관리자"),
    USER("회원")
}