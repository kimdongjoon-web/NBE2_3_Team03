package com.example.echo.global.advice

import com.example.echo.global.api.ApiResponse
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.global.exception.UploadCustomException
import com.example.echo.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import java.io.IOException

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(PetitionCustomException::class)
    fun handlePetitionCustomException(e: PetitionCustomException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("국민동의청원 서비스 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(e.errorCode.httpStatus)
            .body(ApiResponse.error(e.errorCode.message))
    }

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
        log.error("잘못된 입력값: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.INVALID_ARGUMENT.httpStatus)
            .body(ApiResponse.error(ErrorCode.INVALID_ARGUMENT.message))
    }

  @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("회원 권한 접근 제한: ${e.message}", e)
        return ResponseEntity
            .status(ErrorCode.ACCESS_DENIED.httpStatus)
            .body(ApiResponse.error(ErrorCode.ACCESS_DENIED.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("예상치 못한 에러 발생: ${e.message}", e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("예상치 못한 에러로 서버 오류가 발생했습니다."))
    }
}
