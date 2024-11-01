package com.example.echo.domain.inquiry.dto.response

import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.InquiryCategory
import com.example.echo.domain.inquiry.entity.InquiryStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class InquiryResponse (

    @Schema(description = "문의 ID", example = "1")
    val inquiryId: Long?,

    @Schema(description = "회원 ID", example = "user1")
    val memberId: Long?,

    @Schema(description = "문의 카테고리", example = "MEMBER")
    val inquiryCategory: InquiryCategory,

    @Schema(description = "문의 제목", example = "문의 제목 예시")
    val inquiryTitle: String,

    @Schema(description = "문의 내용", example = "문의 내용 예시")
    val inquiryContent: String,

    @Schema(description = "문의 작성일", example = "2024-10-08T12:34:56")
    val createdDate: LocalDateTime?,

    @Schema(description = "답변 내용", example = "답변 내용 예시", nullable = true)
    val replyContent: String? = null,

    @Schema(description = "문의 상태", example = "RESOLVED")
    val inquiryStatus: InquiryStatus,

    @Schema(description = "답변 작성일", example = "2024-10-08T14:56:00", nullable = true)
    val repliedDate: LocalDateTime? = null
) {
    companion object {
        fun from(inquiry: Inquiry): InquiryResponse {
            return InquiryResponse(
                inquiryId = inquiry.inquiryId,
                inquiryTitle = inquiry.inquiryTitle,
                inquiryContent = inquiry.inquiryContent,
                createdDate =inquiry.createdDate,
                replyContent = inquiry.replyContent,
                repliedDate = inquiry.repliedDate,
                inquiryCategory = inquiry.inquiryCategory,
                inquiryStatus = inquiry.inquiryStatus,
                memberId = inquiry.member.memberId
            )
        }
    }
}