package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.AgeGroupInterestCount
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException

data class AgeGroupInterestCountResponse (

    val ageGroup: String,
    val petition: PetitionDTO,
    val interestCount: Int
) {
    companion object {
        fun fromEntity(ageGroupInterestCount: AgeGroupInterestCount): AgeGroupInterestCountResponse {
            val petition = ageGroupInterestCount.petition ?: throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)

            return AgeGroupInterestCountResponse(
                ageGroup = ageGroupInterestCount.ageGroup,
                petition = PetitionDTO(
                    petitionId = petition.petitionId ?: throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND),
                    title = petition.title!!,
                    category = petition.category!!,
                    likesCount = petition.likesCount,
                    interestCount = petition.interestCount,
                    agreeCount = petition.agreeCount!!
                ),
                interestCount = ageGroupInterestCount.interestCount
            )
        }
    }
}