package com.example.echo.global.exception

class PetitionCustomException(
    private val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
