package com.example.echo.global.exception

class UploadCustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)