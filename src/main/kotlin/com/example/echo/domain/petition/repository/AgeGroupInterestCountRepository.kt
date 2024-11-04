package com.example.echo.domain.petition.repository

import com.example.echo.domain.petition.entity.AgeGroupInterestCount
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AgeGroupInterestCountRepository : JpaRepository<AgeGroupInterestCount, Long> {

    @Query("SELECT a FROM AgeGroupInterestCount a WHERE a.ageGroup = :ageGroup ORDER BY a.interestCount DESC")
    fun findTopPetitionsByAgeGroup(ageGroup: String, pageable: Pageable): List<AgeGroupInterestCount>

    @Query("SELECT a FROM AgeGroupInterestCount a WHERE a.ageGroup = :ageGroup AND a.petition.petitionId = :petitionId")
    fun findByAgeGroupAndPetitionId(ageGroup: String, petitionId: Long): AgeGroupInterestCount?
}