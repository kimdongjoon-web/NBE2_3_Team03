package com.example.echo.domain.petition.util

import com.example.echo.domain.petition.dto.response.NewsSearchResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders  // 변경: java.net.http.HttpHeaders -> org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NaverNewsSearchUtil(
    private val restTemplate: RestTemplate
) {
    companion object {
        private const val NAVER_OPEN_API_URL = "https://openapi.naver.com/v1/search/news.json"
    }

    fun searchNews(
        query: String,
        clientId: String,
        clientSecret: String,
        display: Int = 10,
        start: Int = 1,
        sort: String = "sim"
    ): NewsSearchResponse {
        val url = "$NAVER_OPEN_API_URL?query=$query&display=$display&start=$start&sort=$sort"

        val headers = HttpHeaders()  // spring의 HttpHeaders 사용
        headers.set("X-Naver-Client-Id", clientId)
        headers.set("X-Naver-Client-Secret", clientSecret)

        val httpEntity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            httpEntity,
            NewsSearchResponse::class.java
        )

        return response.body ?: throw RuntimeException("Failed to get news from Naver API")
    }
}