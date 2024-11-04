package com.example.echo.domain.petition.service

import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.domain.petition.dto.response.AgeGroupInterestCountResponse
import com.example.echo.domain.petition.entity.AgeGroupInterestCount
import com.example.echo.domain.petition.repository.AgeGroupInterestCountRepository
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgeGroupInterestCountService(
    private val memberRepository: MemberRepository,
    private val ageGroupInterestCountRepository: AgeGroupInterestCountRepository,
    private val petitionRepository: PetitionRepository
) {

    // 나이대 별 관심목록 수 많은 청원 5개 추천
    @Transactional
    fun getTopPetitionsByAgeGroup(member: Member): List<AgeGroupInterestCountResponse> {
        val memberAgeGroup: String = getAgeGroup(member.age)
        val pageable = PageRequest.of(0, 5)
        val topPetitions = ageGroupInterestCountRepository.findTopPetitionsByAgeGroup(memberAgeGroup, pageable)
        return topPetitions.map{AgeGroupInterestCountResponse.fromEntity(it)}
    }

    // 해당 청원, 나이대 +1
    // 관심목록 추가 시 청원 번호와 나이 전달 받기
    @Transactional
    fun addAgeGroupInterestCount(memberId: Long, petitionId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow {
            PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }
        val petition = petitionRepository.findById(petitionId).orElseThrow {
            PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)}

        val ageGroup = getAgeGroup(member.age)
        val count = ageGroupInterestCountRepository.findByAgeGroupAndPetitionId(ageGroup, petitionId)
        if (count != null) {
            count.interestCount += 1
            ageGroupInterestCountRepository.save(count)
        } else { // 기존에 없으면 새로 만들고 count 1 지정
            val newCount = AgeGroupInterestCount(ageGroup = ageGroup, petition = petition, interestCount = 1)
            ageGroupInterestCountRepository.save(newCount)
        }
    }

    // 해당 청원, 나이대 -1
    // 관심목록 삭제 시 청원 번호와 나이 전달 받기
    @Transactional
    fun removeAgeGroupInterestCount(memberId: Long, petitionId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow {
            PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }
        val petition = petitionRepository.findById(petitionId).orElseThrow {
            PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)}

        val ageGroup = getAgeGroup(member.age)
        val count = ageGroupInterestCountRepository.findByAgeGroupAndPetitionId(ageGroup, petitionId)
        if (count != null) {
            count.interestCount -= 1
            ageGroupInterestCountRepository.save(count)
        } else {
            throw PetitionCustomException(ErrorCode.PETITION_NOT_FOUND)
        }
    }


    // 나이 그룹 지정
    private fun getAgeGroup(age: Int) : String {
        return when {
            age in 0..9 -> "10대 미만"
            age in 10..19 -> "10대"
            age in 20..29 -> "20대"
            age in 30..39 -> "30대"
            age in 40..49 -> "40대"
            age in 50..59 -> "50대"
            age in 60..69 -> "60대"
            age in 70..79 -> "70대"
            else -> "80대 이상"

        }
    }
}