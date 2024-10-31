package com.example.echo.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    USER_NOT_MEMBER(HttpStatus.FORBIDDEN, "비회원은 사용할 수 없습니다."),

    // Petition
    PETITION_NOT_FOUND(HttpStatus.NOT_FOUND, "청원을 찾을 수 없습니다."),
    SELENIUM_TIMEOUT(HttpStatus.BAD_REQUEST, "크롤링 도중 시간 초과가 발생했습니다."),
    SELENIUM_NO_ELEMENT_FOUND(HttpStatus.NOT_FOUND, "페이지에서 필요한 요소를 찾을 수 없습니다."),
    SELENIUM_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 크롤링 오류가 발생했습니다."),

    // Inquiry
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "1:1 문의를 찾을 수 없습니다."),
    INQUIRY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "1:1 문의에 대한 접근 권한이 없습니다.")
}
