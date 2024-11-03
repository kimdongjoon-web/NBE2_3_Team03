package com.example.echo.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "naver")
data class NaverProperties @ConstructorBinding constructor(
    val client: Client
) {
    data class Client(
        val id: String,
        val secret: String
    )
}