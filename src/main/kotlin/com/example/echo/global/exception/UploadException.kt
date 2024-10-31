package com.example.echo.global.exception

class UploadException : RuntimeException {
    companion object {
        private const val serialVersionUID: Long = 1L
    }

    // 사용자 정의 메시지를 전달하는 생성자
    constructor(message: String) : super(message)

    // 기본 생성자
    constructor() : super("파일 업로드 중 오류가 발생했습니다.")

    // 특정 원인과 메시지를 전달하는 생성자
    constructor(message: String, cause: Throwable) : super(message, cause)
}
