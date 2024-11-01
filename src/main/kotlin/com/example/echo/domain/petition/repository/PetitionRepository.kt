package com.example.echo.domain.petition.repository

import com.example.echo.domain.petition.dto.response.PetitionResponseDto
import com.example.echo.domain.petition.entity.Category
import com.example.echo.domain.petition.entity.Petition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface PetitionRepository : JpaRepository<Petition, Long> {

    @Query("SELECT p FROM Petition p WHERE p.category = :category AND p.endDate >= CURRENT_DATE")
    fun findByCategory(pageable: Pageable, @Param("category") category: Category): Page<Petition>

    @Query("SELECT p FROM Petition p WHERE p.originalUrl = :originalUrl")
    fun findByUrl(@Param("originalUrl") originUrl: String): Optional<Petition>

    @Query("SELECT p FROM Petition p WHERE p.endDate >= CURRENT_DATE ORDER BY p.endDate ASC")
    fun getEndDatePetitions(pageable: Pageable): List<PetitionResponseDto>

    @Query("SELECT p FROM Petition p WHERE p.endDate >= CURRENT_DATE ORDER BY p.likesCount DESC")
    fun getLikesCountPetitions(pageable: Pageable): List<PetitionResponseDto>

    @Query("SELECT p FROM Petition p WHERE p.category = :category AND p.endDate >= CURRENT_DATE ORDER BY FUNCTION('RAND')")
    fun getCategoryPetitionsInRandomOrder(@Param("category") category: Category, pageable: Pageable): List<PetitionResponseDto>

    // 제목에 검색어가 포함된 청원 조회 메서드 추가
    fun findByTitleContainingIgnoreCase(title: String): List<Petition>

    @Query("SELECT p FROM Petition p WHERE p.endDate >= CURRENT_DATE")
    fun findAllOngoing(pageable: Pageable): Page<Petition>

    @Query("SELECT p FROM Petition p WHERE p.endDate >= CURRENT_DATE")
    fun findAllActive(): List<Petition>
}