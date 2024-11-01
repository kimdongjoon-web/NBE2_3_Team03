package com.example.echo.domain.inquiry.dto.request

import com.example.echo.domain.inquiry.entity.Inquiry
import com.example.echo.domain.inquiry.entity.InquiryCategory
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class InquiryUpdateRequest (

    @field:NotNull(message = "문의 카테고리를 선택해주세요.")
    @Schema(description = "문의 카테고리", example = "PETITION")
    var inquiryCategory: InquiryCategory? = null,

    @field:NotBlank(message = "문의 제목을 입력해주세요.")
    @Schema(description = "문의 제목", example = "청원이 보이지 않는 문제.")
    var inquiryTitle:String? = null,

    @field:NotBlank(message = "문의 내용을 입력해주세요.")
    @Schema(description = "문의 내용", example = "청원 내용이 보이지 않습니다.")
    var inquiryContent:String? = null
) {
    fun updateInquiry(inquiry: Inquiry) {
        inquiryCategory?.let { inquiry.inquiryCategory = it }
        inquiryTitle?.let { inquiry.inquiryTitle = it }
        inquiryContent?.let { inquiry.inquiryContent = it }
    }
}