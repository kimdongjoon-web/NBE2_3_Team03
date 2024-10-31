package com.example.echo.global.exception

class MemberCustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)