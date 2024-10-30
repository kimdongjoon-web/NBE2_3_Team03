package com.example.echo.global.api

data class ApiResponse<T>(
    val message: String,
    val data: T?,
    val success: Boolean
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse("요청이 성공적으로 처리되었습니다.", data, true)

        fun<T> success(message: String, data: T): ApiResponse<T> =
            ApiResponse(message, data, true)

        fun<T> error(message: String): ApiResponse<T> =
            ApiResponse(message, null, false)
    }
}
