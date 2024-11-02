package com.example.echo.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    // Member
    MEMBER_NOT_FOUND(NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "이미 존재하는 이메일입니다."),
    PHONE_ALREADY_EXISTS(CONFLICT, "이미 존재하는 전화번호입니다."),
    USER_NOT_MEMBER(FORBIDDEN, "비회원은 사용할 수 없습니다."),
    INVALID_PASSWORD(UNAUTHORIZED, "잘못된 비밀번호입니다."),
    USERID_ALREADY_EXISTS(CONFLICT, "이미 존재하는 사용자 ID입니다."),
    INVALID_OLD_PASSWORD(BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    SAME_AS_OLD_PASSWORD(BAD_REQUEST, "이전 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    INSUFFICIENT_PERMISSIONS(FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),
    AVATAR_NOT_FOUND(NOT_FOUND, "프로필 아바타를 찾을 수 없습니다."),

    // Petition
    PETITION_NOT_FOUND(NOT_FOUND, "청원을 찾을 수 없습니다."),
    PETITION_EXPIRED(NOT_FOUND, "청원 만료 기간이 지났습니다."),
    SELENIUM_TIMEOUT(BAD_REQUEST, "크롤링 도중 시간 초과가 발생했습니다."),
    SELENIUM_NO_ELEMENT_FOUND(NOT_FOUND, "페이지에서 필요한 요소를 찾을 수 없습니다."),
    SELENIUM_UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "알 수 없는 크롤링 오류가 발생했습니다."),

    // Inquiry
    INQUIRY_NOT_FOUND(NOT_FOUND, "1:1 문의를 찾을 수 없습니다."),
    INQUIRY_ACCESS_DENIED(FORBIDDEN, "1:1 문의에 대한 접근 권한이 없습니다."),

    // Upload
    FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    INVALID_FILE_FORMAT(BAD_REQUEST, "파일 형식이 올바르지 않습니다."),
    THUMBNAIL_CREATION_FAILED(INTERNAL_SERVER_ERROR, "썸네일 생성에 실패했습니다."),
    INVALID_UPLOAD_PATH(INTERNAL_SERVER_ERROR, "업로드 경로가 올바르지 않습니다."),
    FILE_SIZE_LIMIT_EXCEEDED(BAD_REQUEST, "파일 크기 제한을 초과했습니다."),
    FILE_NOT_FOUND(NOT_FOUND, "파일을 찾을 수 없습니다."),
}
