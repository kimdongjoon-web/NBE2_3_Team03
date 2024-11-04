package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.Category

data class PetitionDTO (
    val petitionId: Long,
    val title: String,
    val category: Category,
    val likesCount: Int,
    val interestCount: Int,
    val agreeCount: Int
)