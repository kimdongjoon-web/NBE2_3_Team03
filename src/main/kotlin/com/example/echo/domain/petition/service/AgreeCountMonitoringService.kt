package com.example.echo.domain.petition.service

import com.example.echo.domain.petition.repository.PetitionRepository
import com.example.echo.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgreeCountMonitoringService(

    private val petitionRepository: PetitionRepository,
    private val petitionCrawlService: PetitionCrawlService
) {
    @Transactional
    @Scheduled(fixedRate = 600000L)
    fun updateAgreeCountFromWeb() {
        val petitions = petitionRepository.findAllActive()

        for (petition in petitions) {
            try {
                val currentAgreeCount = petitionCrawlService.fetchAgreeCount(petition.originalUrl!!)

                if (currentAgreeCount > petition.agreeCount!!) {
                    log.info("웹의 동의자 수: $currentAgreeCount, 기존 동의 수: ${petition.agreeCount}")
                    petition.agreeCount = currentAgreeCount
                    petitionRepository.save(petition)
                    log.info("동의 수 업데이터 성공 청원: ${petition.petitionId}")
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
    }
}