package com.example.echo.global.exception

import org.springframework.http.HttpStatus

data class ErrorResponse private constructor(
    val httpStatus: HttpStatus,
    val message: String
) {
    companion object {
        fun from(httpStatus: HttpStatus, message: String): ErrorResponse {
            return ErrorResponse(httpStatus, message)
        }
    }
}