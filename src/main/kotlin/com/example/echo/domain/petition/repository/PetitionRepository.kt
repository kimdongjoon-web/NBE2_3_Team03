package com.example.echo.domain.petition.repository

import com.example.echo.domain.petition.entity.Petition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PetitionRepository: JpaRepository<Petition, Long> {

    @Query("SELECT COUNT(p) FROM Petition p WHERE p.originalUrl = :originalUrl")
    fun findByUrl(@Param("originalUrl") originUrl: String?): Int    // url을 통해 해당 데이터 존재 여부만 판단하므로 count로 변경

}