package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.AgeGroupInterestCount
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException

data class AgeGroupInterestCountResponse (

    val ageGroup: String,
    val petition: Petition,
    val interestCount: Int
) {
    companion object {
        fun fromEntity(ageGroupInterestCount: AgeGroupInterestCount): AgeGroupInterestCountResponse {
            return AgeGroupInterestCountResponse(
                ageGroup = ageGroupInterestCount.ageGroup,
                petition = ageGroupInterestCount.petition ?: throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND),
                interestCount = ageGroupInterestCount.interestCount
            )
        }
    }
}