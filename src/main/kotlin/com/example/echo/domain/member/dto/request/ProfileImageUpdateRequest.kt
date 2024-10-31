package com.example.echo.domain.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class ProfileImageUpdateRequest(

    @field:Schema(description = "업데이트할 프로필 이미지 파일", required = true)
    @field:NotNull(message = "파일은 비어있을 수 없습니다.")
    val avatarImage: MultipartFile
)
