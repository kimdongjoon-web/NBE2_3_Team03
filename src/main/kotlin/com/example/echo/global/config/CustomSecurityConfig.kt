package com.example.echo.global.config

import com.example.echo.global.security.filter.JWTCheckFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class CustomSecurityConfig(
    private val jwtCheckFilter: JWTCheckFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        configureHttpSecurity(http)
        addJwtFilter(http)
        http.cors { it.configurationSource(corsConfigurationSource()) }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfig = createCorsConfiguration()
        return createCorsConfigurationSource(corsConfig)
    }

    private fun configureHttpSecurity(http: HttpSecurity) {
        http
            .formLogin { it.disable() }
            .logout { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.NEVER) }
    }

    private fun addJwtFilter(http: HttpSecurity) {
        http.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    private fun createCorsConfiguration(): CorsConfiguration {
        return CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowedHeaders = listOf("Authorization", "Content-Type", "Cache-Control")
            allowCredentials = true
        }
    }

    private fun createCorsConfigurationSource(corsConfig: CorsConfiguration): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }
    }
}