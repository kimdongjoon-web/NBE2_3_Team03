package com.example.echo.domain.inquiry.repository

import com.example.echo.domain.inquiry.dto.response.InquiryResponse
import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.QInquiry
import com.example.echo.domain.member.entity.QMember
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.repository.query.Param

class InquiryPagingImpl :
    QuerydslRepositorySupport(Inquiry::class.java), InquiryPaging {

    override fun findAllInquiriesUser(@Param("memberId")memberId: Long, pageable: Pageable): Page<Inquiry> {

        val inquiry = QInquiry.inquiry;
        val member = QMember.member

        val query: JPQLQuery<Inquiry> = from(inquiry)
            .leftJoin(inquiry.member, member)
            .where(member.memberId.eq(memberId))
            .select(inquiry)

        querydsl!!.applyPagination(pageable, query)

        val content = query.fetch()
        val total = query.fetchCount()

        return PageImpl(content, pageable, total)
    }


}