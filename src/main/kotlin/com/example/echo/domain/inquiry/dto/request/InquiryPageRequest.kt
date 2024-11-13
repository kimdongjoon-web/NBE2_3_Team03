package com.example.echo.domain.inquiry.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class InquiryPageRequest (

    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지 번호는 최소 1이어야 합니다.")
    @Schema(description = "요청하는 페이지 번호", example = "1")
    var pageNumber: Int? = null,

    @field:NotNull(message = "페이지 사이즈는 필수입니다.")
    @field:Min(value = 5, message = "페이지 사이즈는 최소 5이어야 합니다.")
    @field:Max(value = 20, message = "페이지 사이즈는 최대 20이어야 합니다.")
    @Schema(description = "페이지에 표시할 항목 수", example = "5")
    var pageSize: Int? = null
){
    fun getPageable(): Pageable {
        return PageRequest.of(
            (pageNumber ?: 1) - 1,
            pageSize ?: 5,
            Sort.by("inquiryId").descending())
    }

}