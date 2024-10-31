package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface InquiryRepository : JpaRepository<Inquiry, Long?>, InquiryPaging {

    @Query("select i from Inquiry i join fetch i.member im")
    fun findAllInquiriesAdmin(pageable: Pageable): Page<Inquiry>
}