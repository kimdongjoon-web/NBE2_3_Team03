package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

interface InquiryPaging {
    @Query("SELECT i FROM Inquiry i JOIN FETCH i.member WHERE i.member.memberId = :memberId")
    fun findAllInquiriesUser(memberId: Long, pageable: Pageable): Page<Inquiry>
}