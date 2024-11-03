package com.example.echo.domain.petition.dto.response

import com.example.echo.domain.petition.entity.News
import java.time.ZonedDateTime

data class NewsResponseDto(
    val newsId: Long?,
    val title: String,
    val originalLink: String,
    val link: String,
    val description: String,
    val publishedAt: ZonedDateTime,
    val petitionId: Long?
) {
    companion object {
        fun from(news: News): NewsResponseDto {
            return NewsResponseDto(
                newsId = news.newsId,
                title = news.title,
                originalLink = news.originalLink,
                link = news.link,
                description = news.description,
                publishedAt = news.publishedAt,
                petitionId = news.petition?.petitionId
            )
        }
    }
}