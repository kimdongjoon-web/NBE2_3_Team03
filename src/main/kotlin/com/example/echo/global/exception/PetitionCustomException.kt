package com.example.echo.global.exception

class PetitionCustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message) {
    // 추가적인 생성자나 메서드가 필요할 경우 여기에 작성할 수 있습니다.
}
