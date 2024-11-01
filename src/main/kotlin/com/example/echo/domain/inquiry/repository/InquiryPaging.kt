package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.entity.Inquiry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InquiryPaging {
    fun findAllInquiriesUser(memberId: Long, pageable: Pageable): Page<Inquiry>
}