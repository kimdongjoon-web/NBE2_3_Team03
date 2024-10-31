package com.example.echo.global.security.filter

import com.example.echo.global.security.auth.CustomUserPrincipal
import com.example.echo.global.security.util.JWTUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JWTCheckFilter(
    private val jwtUtil: JWTUtil
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (!request.requestURI.startsWith("/api/")) {
            return true
        }
        return isAuthExcludedPath(request)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val headerAuth = request.getHeader("Authorization")

        if (!isTokenValid(headerAuth)) {
            handleException(response, Exception("ACCESS TOKEN NOT FOUND"))
            return
        }

        val accessToken = headerAuth.substring(7)
        try {
            val claims = jwtUtil.validateToken(accessToken)
            setAuthentication(claims)
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handleException(response, e)
        }
    }

    private fun isAuthExcludedPath(request: HttpServletRequest): Boolean {
        val requestURI = request.requestURI
        val method = request.method

        if (requestURI.startsWith("/api/members/login") || requestURI.startsWith("/api/members/signup")) {
            return true
        }

        if (requestURI.startsWith("/api/petitions") && method.equals("GET", ignoreCase = true)) {
            return requestURI != "/api/petitions/Myinterest"
        }

        return false
    }

    private fun isTokenValid(headerAuth: String?): Boolean {
        return headerAuth?.startsWith("Bearer ") == true
    }

    private fun setAuthentication(claims: Map<String, Any>) {
        val userId = claims["userId"].toString()
        val roles = claims["role"].toString().split(",")
        val memberId = claims["memberId"].toString().toLong()

        val authToken = UsernamePasswordAuthenticationToken(
            CustomUserPrincipal(userId, memberId),
            null,
            roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }
        )

        SecurityContextHolder.getContext().authentication = authToken
    }

    @Throws(IOException::class)
    fun handleException(response: HttpServletResponse, e: Exception) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        response.writer.println("{\"error\": \"${e.message}\"}")
    }
}