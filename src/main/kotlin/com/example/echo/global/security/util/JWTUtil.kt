package com.example.echo.global.security.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTUtil {
    companion object {
        // TODO: 실제 운영 환경에서는 환경 변수나 설정 파일에서 관리해야 함
        private const val SECRET_KEY = "1234567890123456789012345678901234567890"
    }

    // JWT 서명을 위한 비밀 키 생성
    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * JWT 토큰 생성
     * @param valueMap JWT에 저장할 클레임 (payload)
     * @param min 만료 시간 (분 단위)
     * @return 생성된 JWT 토큰
     */
    fun createToken(valueMap: Map<String, Any>, min: Long): String {
        val key = getSigningKey()
        val now = Date()

        return Jwts.builder()
            .header()
            .add("typ", "JWT")
            .and()
            .claims(valueMap)
            .issuedAt(now)
            .expiration(Date(now.time + Duration.ofMinutes(min).toMillis()))
            .signWith(key)
            .compact()
    }

    /**
     * JWT 토큰 검증 및 클레임 추출
     * @param token 검증할 JWT 토큰
     * @return 토큰에서 추출한 클레임
     * @throws io.jsonwebtoken.security.SecurityException 서명이 유효하지 않은 경우
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰이 만료된 경우
     * @throws io.jsonwebtoken.MalformedJwtException 토큰 형식이 잘못된 경우
     */
    fun validateToken(token: String): Map<String, Any> {
        val claims: Claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload

        return claims
    }
}