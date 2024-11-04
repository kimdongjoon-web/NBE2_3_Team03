package com.example.echo.domain.petition.service

import com.example.echo.domain.petition.dto.response.IncreasedPetitionResponse
import com.example.echo.domain.petition.dto.response.PetitionResponseDto
import com.example.echo.domain.petition.entity.Petition
import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.log
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgreeCountMonitoringService(

    private val petitionRepository: PetitionRepository,
    private val petitionCrawlService: PetitionCrawlService,
) {

    // 급증 데이터를 저장할 필드
    private var increasedPetitionsCache: List<Petition> = emptyList()

    @Transactional
    @Scheduled(fixedRate = 86400000L, initialDelay = 200000L)   // 스케줄링 주기 하루, 최초 실행 약 3분 후
    fun updateAgreeCountFromWeb() {
        val petitions = petitionRepository.findAllActive()
        val increasedPetitions = mutableListOf<Pair<Petition, Int>>()  // 증가량을 함께 저장할 리스트

        for (petition in petitions) {
            try {
                val currentAgreeCount = petitionCrawlService.fetchAgreeCount(petition.originalUrl!!)

                val increase = currentAgreeCount - (petition.agreeCount ?: 0) // 증가량 계산

                if (increase > 0) {
                    increasedPetitions.add(Pair(petition, increase)) // 증가한 경우에만 리스트에 추가
                }

                if (currentAgreeCount > petition.agreeCount!!) {
                    log.info("웹의 동의자 수: $currentAgreeCount, 기존 동의 수: ${petition.agreeCount}")
                    petition.previousAgreeCount = petition.agreeCount!!  // 이전 동의자 수 저장
                    petition.agreeCount = currentAgreeCount
                    petitionRepository.save(petition)
                    log.info("동의 수 업데이트 성공 청원: ${petition.petitionId}")
                } else if (currentAgreeCount < petition.agreeCount!!) {
                    log.info("동의 수 업데이트 실패")
                } else {
                    log.info("동의 수 동일")
                }
            } catch (e: Exception) {
                log.error("동의자 수 업데이트 실패 청원: ${petition.petitionId}")
                e.printStackTrace()
            }
        }

        // 증가량 기준으로 내림차순 정렬한 후 캐시에 저장
        increasedPetitionsCache = increasedPetitions
            .sortedByDescending { it.second }
            .map { it.first }
    }

    // 컨트롤러에서 호출할 급증 데이터 조회 메서드
    fun increasedAgreeCountList(): List<IncreasedPetitionResponse> {
        val pageable = PageRequest.of(0, 10)
        return petitionRepository.findPetitionsWithIncreasedAgreeCount(pageable)
    }
}
