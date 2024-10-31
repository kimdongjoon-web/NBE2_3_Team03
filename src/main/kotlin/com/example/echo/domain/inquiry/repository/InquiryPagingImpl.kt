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

    override fun findAllInquiriesUser(@Param("memberId")memberId: Long, pageable: Pageable): Page<InquiryResponse> {

        val inquiry = QInquiry.inquiry;
        val member = QMember.member

        val query: JPQLQuery<InquiryResponse> = from(inquiry)
            .leftJoin(inquiry.member, member)
            .where(member.memberId.eq(memberId))
            .select(
                Projections.bean(
                    InquiryResponse::class.java,
                    inquiry.inquiryId,
                    member.memberId.`as`("memberId"),
                    inquiry.inquiryCategory,
                    inquiry.inquiryTitle,
                    inquiry.inquiryContent,
                    inquiry.createdDate,
                    inquiry.replyContent,
                    inquiry.inquiryStatus,
                    inquiry.repliedDate
                )
            )

        querydsl!!.applyPagination(pageable, query)

        val content = query.fetch()
        val total = query.fetchCount()

        return PageImpl(content, pageable, total)
    }


}