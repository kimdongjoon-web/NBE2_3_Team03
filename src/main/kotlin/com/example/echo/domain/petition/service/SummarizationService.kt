package com.example.echo.domain.petition.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SummarizationService(

    private val restTemplate: RestTemplate
) {
    @Value("\${openai.api.key}")
    private lateinit var apiKey: String

    fun getSummarizedText(text: String): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $apiKey")
            contentType = MediaType.APPLICATION_JSON
        }

        val body = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to "다음 국민청원을 요약하고 주요 포인트를 포함하여 완전한 문장으로 마무리되게 작성해 주세요: $text"
                )
            ),
            "max_tokens" to 300,
            "temperature" to 0.3 // 높을 수록 유연
        )

        val request = HttpEntity(body, headers)
        val response= restTemplate.postForEntity(url, request, Map::class.java)

        // 요약 내용 추출
        val responseBody = response.body ?: throw IllegalStateException("Response body is null")

        val choices = responseBody["choices"] as? List<Map<String, Any>>
            ?: throw IllegalStateException("Choices are null or not a list")

        val message =
            (choices[0]["message"] as? Map<String, Any>) ?: throw IllegalStateException("Message is null or not a map")
        val summary = message["content"] as? String ?: throw IllegalStateException("Content is null or not a string")

        return summary.trim()
    }
}