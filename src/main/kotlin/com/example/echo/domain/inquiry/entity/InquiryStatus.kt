package com.example.echo.domain.inquiry.entity

enum class InquiryStatus (val description: String) {

    PENDING("답변대기중"),
    RESOLVED("답변완료")
}