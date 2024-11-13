package com.example.echo.global.security.auth

import java.security.Principal

class CustomUserPrincipal(
    private val userId: String,
    val memberId: Long
) : Principal {
    override fun getName(): String = userId
}