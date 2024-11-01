package com.example.echo.global.exception

class PetitionCustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
