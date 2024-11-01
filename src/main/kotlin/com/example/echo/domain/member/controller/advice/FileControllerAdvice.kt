package com.example.echo.domain.member.controller.advice

import com.example.echo.global.api.ApiResponse
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.UploadCustomException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import java.io.IOException

@RestControllerAdvice
class FileControllerAdvice {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(UploadCustomException::class)
    fun handleUploadException(e: UploadCustomException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("파일 업로드 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(e.errorCode.httpStatus)
            .body(ApiResponse.error(e.errorCode.message))
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSizeException(e: MaxUploadSizeExceededException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("파일 크기 제한 초과: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.FILE_SIZE_LIMIT_EXCEEDED.httpStatus)
            .body(ApiResponse.error(ErrorCode.FILE_SIZE_LIMIT_EXCEEDED.message))
    }

    @ExceptionHandler(MultipartException::class)
    fun handleMultipartException(e: MultipartException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Multipart 요청 처리 중 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.INVALID_FILE_FORMAT.httpStatus)
            .body(ApiResponse.error(ErrorCode.INVALID_FILE_FORMAT.message))
    }

    @ExceptionHandler(IOException::class)
    fun handleIOException(e: IOException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("파일 입출력 중 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.FILE_UPLOAD_FAILED.httpStatus)
            .body(ApiResponse.error(ErrorCode.FILE_UPLOAD_FAILED.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("잘못된 파일 형식: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.INVALID_FILE_FORMAT.httpStatus)
            .body(ApiResponse.error(ErrorCode.INVALID_FILE_FORMAT.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("예상치 못한 파일 처리 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.FILE_UPLOAD_FAILED.httpStatus)
            .body(ApiResponse.error("파일 처리 중 오류가 발생했습니다."))
    }
}
